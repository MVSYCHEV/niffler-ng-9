package guru.qa.niffler.data.dao;

import guru.qa.niffler.data.entity.userdata.UserDataUserEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserDataUserDao {
	UserDataUserEntity createUser(UserDataUserEntity user);

	Optional<UserDataUserEntity> findById(UUID id);

	Optional<UserDataUserEntity> findByUsername(String username);

	List<UserDataUserEntity> findAll();

	void delete(UserDataUserEntity user);
}
