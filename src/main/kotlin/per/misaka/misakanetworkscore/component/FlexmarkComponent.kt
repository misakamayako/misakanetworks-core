package per.misaka.misakanetworkscore.component

import com.vladsch.flexmark.ext.tables.TablesExtension
import com.vladsch.flexmark.ext.toc.TocExtension
import com.vladsch.flexmark.html.HtmlRenderer
import com.vladsch.flexmark.parser.Parser
import com.vladsch.flexmark.util.ast.KeepType
import com.vladsch.flexmark.util.ast.NodeVisitor
import com.vladsch.flexmark.util.data.DataSet
import com.vladsch.flexmark.util.data.MutableDataSet
import com.vladsch.flexmark.util.misc.Extension
import org.springframework.stereotype.Component
import java.util.List

@Component
class FlexmarkComponent {
    fun baseOptions(): DataSet {
        return MutableDataSet()
            .set<KeepType?>(Parser.REFERENCES_KEEP, KeepType.LAST)
            .set<Int?>(HtmlRenderer.INDENT_SIZE, 2)
            .set<Boolean?>(HtmlRenderer.PERCENT_ENCODE_URLS, true)
            .set<Boolean?>(TablesExtension.COLUMN_SPANS, false)
            .set<Boolean?>(TablesExtension.APPEND_MISSING_COLUMNS, true)
            .set<Boolean?>(TablesExtension.DISCARD_EXTRA_COLUMNS, true)
            .set<Boolean?>(TablesExtension.HEADER_SEPARATOR_COLUMN_MATCH, true)
            .set<MutableCollection<Extension?>?>(Parser.EXTENSIONS, List.of<Extension?>(TablesExtension.create(),TocExtension.create()))
            .toImmutable();

    }

    private fun parse(options: DataSet): Parser {
        return Parser.builder(options).build()
    }

    fun renderToHtml(content: String, vararg visitor: NodeVisitor?): String {
        return this.renderToHtml(content, null, visitor = visitor)
    }

    fun renderToHtml(content: String, options: DataSet? = null, vararg visitor: NodeVisitor?): String {
        val dataSet = options ?: baseOptions()
        val parser = parse(dataSet)
        val render = HtmlRenderer.builder(dataSet).build()
        val document = parser.parse(content)
        visitor.forEach {
            it?.visit(document)
        }
        val html = render.render(document)
        return html
    }
}