package guru.qa.niffler.data.repository;

import guru.qa.niffler.data.entity.userdata.UserEntity;

import java.util.Optional;
import java.util.UUID;

public interface UserDataUserRepository {

	UserEntity create(UserEntity user);

	Optional<UserEntity> findById(UUID id);

	Optional<UserEntity> findByUsername(String username);

	UserEntity update(UserEntity user);

	void sendInvitation(UserEntity requester, UserEntity addressee);

	void addFriend(UserEntity requester, UserEntity addressee);

	void remove(UserEntity user);
}
