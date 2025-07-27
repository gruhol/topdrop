package pl.thinkdata.droptop.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class GetPublicationsDto {
    private int pageSize;
    private int pageNo;
    private LocalDateTime lastChangeDate;
    private int transactionNumber;
}
