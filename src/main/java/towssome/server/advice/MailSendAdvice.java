package towssome.server.advice;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import towssome.server.entity.EmailVerification;
import towssome.server.exception.EmailSendException;
import towssome.server.exception.ExpirationEmailException;
import towssome.server.repository.EmailVerificationRepository;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class MailSendAdvice {

    private final JavaMailSender mailSender;
    private final EmailVerificationRepository emailVerificationRepository;

    public boolean CheckAuthNum(String email,int authNum){
        EmailVerification emailVerification = emailVerificationRepository.findByEmail(email).orElseThrow(
                () -> new ExpirationEmailException("이메일 인증 시간이 만료되었습니다")
        );
        return emailVerification.getAuthNum() == authNum;
    }

    public int makeRandomNumber() {
        Random r = new Random();
        String randomNumber = "";
        for(int i = 0; i < 6; i++) {
            randomNumber += Integer.toString(r.nextInt(10));
        }

        return Integer.parseInt(randomNumber);
    }

    public int joinEmail(String email) {
        int authNum = makeRandomNumber();
        String setFrom = "goochul175465@gmail.com"; // email-config에 설정한 자신의 이메일 주소를 입력
        String toMail = email;
        String title = "회원 가입 인증 이메일 입니다."; // 이메일 제목
        String content =
                "품품에 회원가입 해주셔서 감사합니다." + 	//html 형식으로 작성 !
                        "<br><br>" +
                        "인증 번호는 <strong>" + authNum + "</strong> 입니다." +
                        "<br>" +
                        "인증번호를 제대로 입력해주세요"; //이메일 내용 삽입
        mailSend(setFrom, toMail, title, content);
        emailVerificationRepository.save(new EmailVerification(
                email,
                authNum
        ));
        return authNum;
    }

    public void sendFindUsername(String email, String username) {
        String setFrom = "goochul175465@gmail.com"; // email-config에 설정한 자신의 이메일 주소를 입력
        String toMail = email;
        String title = "[품품] 아이디 찾기 이메일 입니다."; // 이메일 제목
        String content =
                "<h1>안녕하세요. 품품을 이용해 주셔서 감사합니다</h1>" + 	//html 형식으로 작성 !
                        "<hr>" +
                        "회원님의 아이디는 <strong>" + username + "</strong> 입니다." +
                        "<br>" +
                        "감사합니다."; //이메일 내용 삽입
        mailSend(setFrom, toMail, title, content);
    }

    private void mailSend(String setFrom, String toMail, String title, String content) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message,true,"utf-8");//이메일 메시지와 관련된 설정을 수행합니다.
            // true를 전달하여 multipart 형식의 메시지를 지원하고, "utf-8"을 전달하여 문자 인코딩을 설정
            helper.setFrom(setFrom);//이메일의 발신자 주소 설정
            helper.setTo(toMail);//이메일의 수신자 주소 설정
            helper.setSubject(title);//이메일의 제목을 설정
            helper.setText(content,true);//이메일의 내용 설정 두 번째 매개 변수에 true를 설정하여 html 설정으로한다.
            mailSender.send(message);
        } catch (MessagingException e) {//이메일 서버에 연결할 수 없거나, 잘못된 이메일 주소를 사용하거나, 인증 오류가 발생하는 등 오류
            // 이러한 경우 MessagingException이 발생
            throw new EmailSendException("이메일을 보내지 못했습니다");
        }


    }

}
