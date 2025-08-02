package guru.qa.niffler.data.dao;

import guru.qa.niffler.data.entity.auth.AuthorityEntity;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public interface AuthAuthorityDao {
	void create(AuthorityEntity... authority);

	@Nonnull
	List<AuthorityEntity> findAll();
}
