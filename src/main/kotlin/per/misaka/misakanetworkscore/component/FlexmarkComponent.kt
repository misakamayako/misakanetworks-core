package per.misaka.misakanetworkscore.component

import com.vladsch.flexmark.ext.autolink.AutolinkExtension
import com.vladsch.flexmark.ext.tables.TablesExtension
import com.vladsch.flexmark.ext.typographic.TypographicExtension
import com.vladsch.flexmark.html.HtmlRenderer
import com.vladsch.flexmark.parser.Parser
import com.vladsch.flexmark.util.ast.NodeVisitor
import com.vladsch.flexmark.util.data.MutableDataSet
import org.springframework.stereotype.Component

@Component
class FlexmarkComponent {
    fun baseOptions(): MutableDataSet {
        return MutableDataSet()
            .set(
                Parser.EXTENSIONS, listOf(
                    AutolinkExtension.create(),
                    TablesExtension.create(),
                    TypographicExtension.create()
                )
            )
            .set(HtmlRenderer.SOFT_BREAK, "<br />\n")
            .set(HtmlRenderer.PERCENT_ENCODE_URLS, true)

    }

    private fun parse(options: MutableDataSet?): Parser {
        return Parser.builder(options).build()
    }

    private fun getRender() = HtmlRenderer.builder().build()

    fun renderToHtml(content: String, vararg visitor: NodeVisitor): String {
        return this.renderToHtml(content, null, visitor = visitor)
    }

    fun renderToHtml(content: String, options: MutableDataSet? = null, vararg visitor: NodeVisitor): String {
        val parser = parse(options ?: baseOptions())
        val render = HtmlRenderer.builder().build()
        val document = parser.parse(content)
        visitor.forEach {
            it.visit(document)
        }
        val html = render.render(document)
        return html
    }
}