package pl.thinkdata.droptop.baselinker.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import pl.thinkdata.droptop.api.model.Category;
import pl.thinkdata.droptop.api.repository.CategoryRepository;
import pl.thinkdata.droptop.baselinker.dto.addCategory.AddCategoryDto;
import pl.thinkdata.droptop.baselinker.dto.addCategory.AddCategoryRequest;
import pl.thinkdata.droptop.baselinker.dto.addCategory.AddCategoryResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
public class AddCategoryProductBaselinkerService extends BaselinkerService implements BaselinkerSendable<AddCategoryResponse, AddCategoryRequest> {

    private final CategoryRepository categoryRepository;
    private final GetCategoryProductBaselinkerService getCategoryProductBaselinkerService;

    protected String methodName;

    @PostConstruct
    private void initMethodName() {
        super.methodName = "addInventoryCategory";
    }


    public List<AddCategoryResponse> sendCategories() {
        String mainCatBaseLinker = getCategoryProductBaselinkerService.getIdMainCategory().toString();
        return categoryRepository.findAll().stream()
                .filter(id -> isNull(id.getBaselinkerId()))
                .map(cat -> createAddCategoryRequest(cat, mainCatBaseLinker))
                .map(this::sendRequest)
                .toList();
    }

    @Override
    public AddCategoryResponse sendRequest(AddCategoryRequest request) {
        try {
            String jsonParams = new ObjectMapper().writeValueAsString(request.getDto());
            ResponseEntity<String> response = getDataFromWebClient(jsonParams);
            return Optional.ofNullable(response)
                    .map(res -> {
                        AddCategoryResponse addCategoryResponse = mapToResponse(res, AddCategoryResponse.class);
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

    private AddCategoryRequest createAddCategoryRequest(Category category, String mainCategory) {

        return AddCategoryRequest.builder()
                .dto(createCategoryDto(category, mainCategory))
                .category(category)
                .build();
    }

    private AddCategoryDto createCategoryDto(Category category, String mainCategory) {
        return AddCategoryDto.builder()
                .name(category.getName())
                .parent_id(category.getParent() == null
                        ? mainCategory
                        : category.getParent().getBaselinkerId())
                .build();
    }
}
