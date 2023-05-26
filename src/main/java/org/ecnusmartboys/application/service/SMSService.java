package org.ecnusmartboys.application.service;

import org.ecnusmartboys.application.dto.request.Common;
import org.ecnusmartboys.application.dto.request.command.SendSMSRequest;
import org.ecnusmartboys.application.dto.response.Responses;

public interface SMSService {
    Responses<String> sendSMS(SendSMSRequest req, Common common);
}
