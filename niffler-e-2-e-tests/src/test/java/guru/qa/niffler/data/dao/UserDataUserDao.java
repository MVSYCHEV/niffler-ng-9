package guru.qa.niffler.data.dao;

import guru.qa.niffler.data.entity.userdata.UserEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserDataUserDao {
	UserEntity createUser(UserEntity user);

	Optional<UserEntity> findById(UUID id);

	Optional<UserEntity> findByUsername(String username);

	List<UserEntity> findAll();

	void delete(UserEntity user);
}
