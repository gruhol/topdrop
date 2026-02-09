package pl.thinkdata.droptop.baselinker.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import pl.thinkdata.droptop.baselinker.dto.getCategory.CategoryBaseLinker;
import pl.thinkdata.droptop.baselinker.dto.EmptyRequest;
import pl.thinkdata.droptop.baselinker.dto.getCategory.GetCategoryResponse;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GetCategoryProductBaselinkerService extends BaselinkerService implements BaselinkerSendable<GetCategoryResponse, EmptyRequest>{

    protected String methodName;

    @PostConstruct
    private void initMethodName() {
        super.methodName = "getInventoryCategories";
    }

    public Long getIdMainCategory() {
        GetCategoryResponse response = sendRequest(new EmptyRequest());
        return response.getCategories().stream()
                .filter(parent -> parent.getParentId() == 0)
                .map(CategoryBaseLinker::getCategoryId)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Błąd pobrania głownej kategorii z baselinkera"));
    }

    @Override
    public GetCategoryResponse sendRequest(EmptyRequest request) {

        String jsonParams = "{}";
        ResponseEntity<String> response = getDataFromWebClient(jsonParams);
        return Optional.ofNullable(response)
                .map(res -> mapToResponse(res, GetCategoryResponse.class))
                .orElseThrow(() -> new RuntimeException("Error baselinker api"));
    }
}
