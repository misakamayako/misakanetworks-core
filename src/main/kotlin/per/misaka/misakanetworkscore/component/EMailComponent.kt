//package per.misaka.misakanetworkscore.component
//
//import org.springframework.mail.SimpleMailMessage
//import org.springframework.mail.javamail.JavaMailSenderImpl
//import org.springframework.stereotype.Component
//
//@Component
//class EMailComponent(private val mailSender: JavaMailSenderImpl) {
//    fun sendSimpleMail(from: String, to: String, subject: String?, text: String) {
//        val simpleMailMessage = SimpleMailMessage()
//        simpleMailMessage.from = from
//        simpleMailMessage.setTo(to)
//        simpleMailMessage.subject = subject
//        simpleMailMessage.text = text
//        mailSender.send(simpleMailMessage)
//    }
//}