package guru.qa.niffler.data.repository.impl.hibernate;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.userdata.FriendshipStatus;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.repository.UserDataUserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.jpa.EntityManagers.em;

public class UserdataUserRepositoryHibernate implements UserDataUserRepository {
	private final EntityManager entityManager = em(Config.getInstance().userdataJdbcUrl());

	@Override
	public UserEntity create(UserEntity user) {
		entityManager.joinTransaction();
		entityManager.persist(user);
		return user;
	}

	@Override
	public Optional<UserEntity> findById(UUID id) {
		return Optional.ofNullable(
				entityManager.find(UserEntity.class, id)
		);
	}

	@Override
	public Optional<UserEntity> findByUsername(String username) {
		try {
			return Optional.of(
					entityManager.createQuery("select u from UserEntity u where u.username =: username", UserEntity.class)
							.setParameter("username", username)
							.getSingleResult()
			);
		} catch (NoResultException e) {
			return Optional.empty();
		}
	}

	@Override
	public UserEntity update(UserEntity user) {
		entityManager.joinTransaction();
		return entityManager.merge(user);
	}

	@Override
	public void sendInvitation(UserEntity requester, UserEntity addressee) {
		entityManager.joinTransaction();
		requester.addFriends(FriendshipStatus.PENDING, addressee);
		entityManager.merge(requester);
		entityManager.merge(addressee);
	}

	@Override
	public void addFriend(UserEntity requester, UserEntity addressee) {
		entityManager.joinTransaction();
		requester.addFriends(FriendshipStatus.ACCEPTED, addressee);
		addressee.addFriends(FriendshipStatus.ACCEPTED, requester);
		entityManager.merge(requester);
		entityManager.merge(addressee);
	}

	@Override
	public void remove(UserEntity user) {
		entityManager.joinTransaction();
		UserEntity attachedUser = entityManager.merge(user);
		entityManager.remove(attachedUser);
	}
}
