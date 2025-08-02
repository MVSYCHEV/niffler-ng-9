package guru.qa.niffler.model.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;

@ParametersAreNonnullByDefault
public record AuthorityJson(
		@JsonProperty("id")
		UUID id,
		@JsonProperty("authority")
		Authority authority,
		@JsonProperty("userId")
		UUID userId) {

	@Nonnull
	public static AuthorityJson fromEntity(AuthorityEntity entity) {
		return new AuthorityJson(
				entity.getId(),
				entity.getAuthority(),
				entity.getUser().getId()
		);
	}
}
