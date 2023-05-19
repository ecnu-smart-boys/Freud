package org.ecnusmartboys.application.service;

import org.ecnusmartboys.application.dto.request.Common;
import org.ecnusmartboys.application.dto.request.command.SendSMSRequest;
import org.ecnusmartboys.application.dto.response.Response;

public interface SMSService {
    Response<String> sendSMS(SendSMSRequest req, Common common);
}
