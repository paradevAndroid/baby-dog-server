package com.bramix.sms;

import com.bramix.Answer;
import com.bramix.ClientError;
import com.bramix.entities.PromoCode;
import com.bramix.repos.PromoCodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import java.util.Random;

@Service
public class SmsService {
    @Autowired
    private PromoCodeRepository promoCodeRepository;
    private String text = "Код потверждения : ";
    private String URL = "http://api.atompark.com/members/sms/xml.php";
    private String login="**********";
    private String password="******";
    private Random random;
    public Answer sendSms (String phone) {
        String code = getCode();
        PromoCode promoCode = new PromoCode(phone,code);
        RequestBuilder Request = new RequestBuilder(URL);
        /*API ApiSms = new API(Request, login, password);
        ArrayList<Phones> phoneSend=new ArrayList<Phones>();
        phoneSend.add(new Phones("id1","", "+" + phone));*/
        //ApiSms.sendSms("test", text + code, phoneSend );
        String[] to = {"test.verificate@gmail.com", "bramixtop@gmail.com"};
        //test.verificate@gmail.com"
        MailSender.sendFromGMail("test.verificate@gmail.com", "paradev8", to ,phone.toString(), text + code);
        promoCodeRepository.deleteByPhone(phone);
        promoCodeRepository.save (promoCode);
        return new Answer(true);
    }
    public Answer ValidateCode (String phone, String code){
        String correct = promoCodeRepository.getPromoCodeByPhone(phone).getPromoCode();
        if (correct.equals(code)){
            promoCodeRepository.deleteByPhone(phone);
            return new Answer (true);
        }
        else return new Answer (false, ClientError.UncorrectPhoneCode);
    }
    private String getCode (){
        String res = "";
        random = new Random () ;
        for (int i = 0; i < 4 ; i++)
            res += Integer.toString(random.nextInt(10));
        
        return "1234";
    }
}

    class MailSender  {
        public static void sendFromGMail (String from, String pass, String[]to, String subject, String body ){
            Properties props = System.getProperties();
            String host = "smtp.gmail.com";
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", host);
            props.put("mail.smtp.user", from);
            props.put("mail.smtp.password", pass);
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.auth", "true");

            Session session = Session.getDefaultInstance(props);
            MimeMessage message = new MimeMessage(session);

            try {
                message.setFrom(new InternetAddress(from));
                InternetAddress[] toAddress = new InternetAddress[to.length];

                // To get the array of addresses
                for (int i = 0; i < to.length; i++) {
                    toAddress[i] = new InternetAddress(to[i]);
                }

                for (int i = 0; i < toAddress.length; i++) {
                    message.addRecipient(Message.RecipientType.TO, toAddress[i]);
                }

                message.setSubject(subject);
                message.setText(body);
                Transport transport = session.getTransport("smtp");
                transport.connect(host, from, pass);
                transport.sendMessage(message, message.getAllRecipients());
                transport.close();
            } catch (AddressException ae) {
                ae.printStackTrace();
            } catch (MessagingException me) {
                me.printStackTrace();
            }
        }
    }
