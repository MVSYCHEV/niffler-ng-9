package guru.qa.niffler.data.dao;

import guru.qa.niffler.data.entity.spend.SpendEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpendDao {
	SpendEntity create(SpendEntity spend);

	SpendEntity update(SpendEntity spend);

	Optional<SpendEntity> findSpendById(UUID id);

	List<SpendEntity> findAllByUsername(String username);

	Optional<SpendEntity> findByUsernameAndSpendDescription(String username, String description);

	List<SpendEntity> findAll();

	void deleteSpend(SpendEntity spend);
}
