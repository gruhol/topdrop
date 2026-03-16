package pl.thinkdata.droptop.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.thinkdata.droptop.api.model.Category;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByNameAndParent(String name, Category parent);

    List<Category> findTop50ByBaselinkerIdIsNull();

    Optional<Category> getCategoryByName(String name);
}
