package build.dream.catering.constants;

public class ConfigurationKeys extends build.dream.common.constants.ConfigurationKeys {
    /**
     * 延时主题
     */
    public static final String DELAYED_TOPIC = "delayed.topic";
    /**
     * 延时队列
     */
    public static final String DELAYED_QUEUE = "delayed.queue";
    /**
     * 延时交换机
     */
    public static final String DELAYED_EXCHANGE = "delayed.exchange";
    /**
     * 延时交换机到延时队列的路由键
     */
    public static final String DELAYED_EXCHANGE_TO_DELAYED_QUEUE_ROUTING_KEY = "delayed.exchange.to.delayed.queue.routing.key";

    public static final String MEI_TUAN_APP_ID = "mei.tuan.app.id";
    public static final String MEI_TUAN_CONSUMER_SECRET = "mei.tuan.consumer.secret";
    public static final String MEI_TUAN_SERVICE_URL = "mei.tuan.service.url";
    public static final String MEI_TUAN_ERP_SERVICE_URL = "mei.tuan.erp.service.url";
    public static final String MEI_TUAN_DEVELOPER_ID = "mei.tuan.developer.id";
    public static final String MEI_TUAN_SIGN_KEY = "mei.tuan.sign.key";

    public static final String ANUBIS_SERVICE_URL = "anubis.service.url";

    /**
     * 消息队列消息主题
     *
     * @see #ORDER_INVALID_MESSAGE_TOPIC: 订单失效
     * @see #OFFLINE_PAY_ALIPAY_ASYNC_NOTIFY_MESSAGE_TOPIC: 线下支付支付宝异步通知消息主题
     * @see #OFFLINE_PAY_UMPAY_ASYNC_NOTIFY_MESSAGE_TOPIC: 线下支付联动异步通知消息主题
     * @see #OFFLINE_PAY_REFUND_ALIPAY_ASYNC_NOTIFY_MESSAGE_TOPIC: 线下支付支付宝退款异步通知消息主题
     * @see #OFFLINE_PAY_REFUND_WEI_XIN_ASYNC_NOTIFY_MESSAGE_TOPIC: 线下支付微信退款异步通知消息主题
     * @see #DADA_ORDER_CALLBACK_MESSAGE_TOPIC: 达达订单回调消息主题
     */
    public static final String ORDER_INVALID_MESSAGE_TOPIC = "order.invalid.message.topic";
    public static final String OFFLINE_PAY_ALIPAY_ASYNC_NOTIFY_MESSAGE_TOPIC = "offline.pay.alipay.async.notify.message.topic";
    public static final String OFFLINE_PAY_UMPAY_ASYNC_NOTIFY_MESSAGE_TOPIC = "offline.pay.umpay.async.notify.message.topic";
    public static final String OFFLINE_PAY_REFUND_ALIPAY_ASYNC_NOTIFY_MESSAGE_TOPIC = "offline.pay.refund.alipay.async.notify.message.topic";
    public static final String OFFLINE_PAY_REFUND_WEI_XIN_ASYNC_NOTIFY_MESSAGE_TOPIC = "offline.pay.refund.wei.xin.async.notify.message.topic";
    public static final String DADA_ORDER_CALLBACK_MESSAGE_TOPIC = "dada.order.callback.message.topic";

    public static final String DINGTALK_ERROR_NOTIFY_CHAT_ID = "dingtalk.error.notify.chat.id";

    public static final String WEI_XIN_API_URL = "wei.xin.api.url";

    public static final String WEI_XIN_OPEN_PLATFORM_APPLICATION_APP_ID = "wei.xin.open.platform.application.app.id";
    public static final String WEI_XIN_OPEN_PLATFORM_APPLICATION_APP_SECRET = "wei.xin.open.platform.application.app.secret";
}
