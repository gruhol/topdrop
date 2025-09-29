package pl.thinkdata.droptop.baselinker.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import pl.thinkdata.droptop.api.model.Category;
import pl.thinkdata.droptop.api.repository.CategoryRepository;
import pl.thinkdata.droptop.baselinker.dto.AddCategoryDto;
import pl.thinkdata.droptop.baselinker.dto.AddCategoryRequest;
import pl.thinkdata.droptop.baselinker.dto.AddCategoryResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AddCategoryProductBaselinkerService extends BaselinkerService implements BaselinkerSendable<AddCategoryResponse, AddCategoryRequest> {

    private final CategoryRepository categoryRepository;

    protected String methodName;

    @PostConstruct
    private void initMethodName() {
        super.methodName = "addInventoryCategory";
    }


    public List<AddCategoryResponse> sendCategories() {

        return categoryRepository.findAll().stream()
                .map(this::createAddCategoryRequest)
                .map(this::sendRequest)
                .toList();
    }

    private AddCategoryRequest createAddCategoryRequest(Category category) {
        return AddCategoryRequest.builder()
                .dto(createCategoryDto(category))
                .category(category)
                .build();
    }

    @Override
    public AddCategoryResponse sendRequest(AddCategoryRequest request) {
        try {
            String jsonParams = new ObjectMapper().writeValueAsString(request.getDto());
            ResponseEntity<String> response = getDataFromWebClient(jsonParams);
            return Optional.ofNullable(response)
                    .map(res -> {
                        AddCategoryResponse addCategoryResponse = mapToAddCategoryResponse(res);
                        request.getCategory().setBaselinkerId(addCategoryResponse.getCategory_id());
                        request.getCategory().setSendDate(LocalDateTime.now());
                        categoryRepository.save(request.getCategory());
                        return addCategoryResponse;
                    })
                    .orElseThrow(() -> new RuntimeException("Error baselinker api"));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Serialization Error");
        }
    }

    private AddCategoryDto createCategoryDto(Category category) {
        return AddCategoryDto.builder()
                .name(category.getName())
                .parent_id(category.getParent() == null ? "4554825" : category.getParent().getBaselinkerId())
                .build();
    }

    private AddCategoryResponse mapToAddCategoryResponse(ResponseEntity<String> response) {
        try {
            return new ObjectMapper().readValue(response.getBody(), AddCategoryResponse.class);
        } catch (Exception e) {
            throw new RuntimeException("Deserialization Error", e);
        }
    }
}
