
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ed.collaborate.service;

import java.util.List;
import javax.mail.MessagingException;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Class which allows the sending of email from a template
 * @author Richard Good
 */
@Service
public class MailTemplateService implements BeanFactoryAware {

    private static Logger logger = Logger.getLogger(MailTemplateService.class);

    @Value("${mail.sendmail:true}")
    private boolean sendEmail;

    @Autowired
    private JavaMailSender mailSender;

    private BeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory bf) throws BeansException {
        beanFactory = bf;
    }

    // Runnable child class which allows the asynchronous sending of email
    private class MailTask implements Runnable {
        private String from;
        private List<String> to;
        private String subject;
        private String[] substitutions;
        private String template;

        public MailTask(String from, List<String> to, String subject, String[] substitutions, String template) {
            this.from=from;
            this.to=to;
            this.subject=subject;
            this.substitutions=substitutions;
            this.template=template;
        }

        /*
         * The runnable method to send the email message
         */
        @Override
        public void run() {
            try
            {
                SimpleMailMessage message = new SimpleMailMessage((SimpleMailMessage)beanFactory.getBean(template));

                if (from!=null)
                {
                    message.setFrom(from);
                }
                if (to!=null)
                {
                    String[] toArray = this.to.toArray(new String[this.to.size()]);
                    message.setTo(toArray);
                }
                if (subject!=null)
                {
                    message.setSubject(subject);
                }

                if (substitutions!=null)
                {
                    message.setText(String.format(message.getText(), substitutions));
                }

                mailSender.send(message);
            }
            catch (Exception e)
            {
                logger.error("Exception caught",e);
            }
        }
    }

    private TaskExecutor taskExecutor;

    // Instantiate a task executor MailService
    public MailTemplateService(TaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    /**
     * Public method to execute an asynchronous email send
     * @param from
     * @param to
     * @param subject
     * @param substitutions
     * @param template
     * @throws MessagingException
     */
    public void sendEmailUsingTemplate(String from, List<String> to, String subject, String[] substitutions, String template)
            throws MessagingException {
        logger.debug("sendEmailUsingTemplate called");
        if (sendEmail) {
            taskExecutor.execute(new MailTask(from,to,subject,substitutions,template));
        } else {
            logger.debug("DEBUG: application would send notification email to: " + to.toString() + ", from: " + from);
        }
    }
}
