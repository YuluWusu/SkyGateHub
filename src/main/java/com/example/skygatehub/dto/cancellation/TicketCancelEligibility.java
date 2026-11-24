package com.example.skygatehub.dto.cancellation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketCancelEligibility {
    private boolean allowed;
    private boolean freeCancel;
    private long daysBefore;
    private BigDecimal estimatedRefund;
    private BigDecimal estimatedForfeit;
    private String policyLabel;
    private String reasonIfDenied;
    private String existingRequestStatus;
    private String existingRequestNote;
}
