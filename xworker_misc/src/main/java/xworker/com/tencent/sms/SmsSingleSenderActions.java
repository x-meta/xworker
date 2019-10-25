package xworker.com.tencent.sms;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONException;
import org.xmeta.ActionContext;
import org.xmeta.Thing;

import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.SmsSingleSenderResult;
import com.github.qcloudsms.httpclient.HTTPException;

public class SmsSingleSenderActions {
	public static SmsSingleSenderResult sendSingleSms(ActionContext actionContext) throws JSONException, HTTPException, IOException {
		Thing self = actionContext.getObject("self");
		int appId = self.doAction("getAppId", actionContext);
		String appKey = self.doAction("getAppKey", actionContext);
		String phoneNumber = self.doAction("getPhoneNumber", actionContext);
		String nationCode = self.doAction("getNationCode", actionContext);
		int templateId = self.doAction("getTemplateId", actionContext);
		ArrayList<String> params = self.doAction("getParams", actionContext);
		String sign = self.doAction("getSign", actionContext);
		
		SmsSingleSender ssender = new SmsSingleSender(appId, appKey);
		SmsSingleSenderResult result = ssender.sendWithParam(nationCode, phoneNumber,
		      templateId, params, sign, "", "");
		
		if(result.result == 0) {
			self.doAction("onSuccess", actionContext, "result", result);
		}else {
			self.doAction("onError", actionContext, "result", result);
		}

		return result;
	}
}
