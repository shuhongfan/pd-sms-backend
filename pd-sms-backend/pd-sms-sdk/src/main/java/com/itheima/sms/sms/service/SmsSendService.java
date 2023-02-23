package com.itheima.sms.sms.service;

import com.itheima.sms.sms.dto.R;
import com.itheima.sms.sms.dto.SmsBatchParamsDTO;
import com.itheima.sms.sms.dto.SmsParamsDTO;

public interface SmsSendService {
    R sendSms(SmsParamsDTO smsParamsDTO);

    R batchSendSms(SmsBatchParamsDTO smsBatchParamsDTO);
}
