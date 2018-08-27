package silicon.thread;

import com.fasterxml.jackson.databind.JsonNode;
import silicon.handler.IdentityMind;
import silicon.handler.MailHandler;
import silicon.model.IMResponse;
import silicon.model.Subscriber;
import silicon.service.IMResponseService;
import silicon.service.SubscriberService;

public class IdentityMindThread extends Thread {

    private Subscriber subscriber;

    private SubscriberService subscriberService;

    private IMResponseService imResponseService;


    public IdentityMindThread(Subscriber subscriber, SubscriberService subscriberService, IMResponseService imResponseService) {
        this.subscriber = subscriber;
        this.subscriberService = subscriberService;
        this.imResponseService = imResponseService;
    }

    public Subscriber getSubscriber() {
        return subscriber;
    }

    public void setSubscriber(Subscriber subscriber) {
        this.subscriber = subscriber;
    }

    public void run() {
        //create transaction
        JsonNode resposeIM = IdentityMind.createSubscriberAsConsumer(subscriber);
        if(resposeIM != null){
            String transactionId = resposeIM.get("tid").asText();
            String statusIM = resposeIM.get("state").asText();
            subscriber.setTransactionId(transactionId);
            subscriber.setStatusIM(statusIM);
            /*if(subscriber.getStatusIM().equals(subscriber.STATUS_ACCEPTED)){
                subscriber.setStatus(subscriber.getStatusIM());
                MailHandler.sendStatusChange(subscriber);
            }*/
            subscriberService.save(subscriber);

            //save IMresponse
            IMResponse imResponse = new IMResponse(resposeIM.toString(), subscriber);
            imResponseService.save(imResponse);
        }
    }
}
