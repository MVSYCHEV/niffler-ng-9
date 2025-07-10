package guru.qa.niffler.data.entity.userdata;

import guru.qa.niffler.model.spend.CurrencyValues;
import guru.qa.niffler.model.userdata.UserJson;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Getter
@Setter
public class UserDataUserEntity implements Serializable {
	private UUID id;
	private String username;
	private CurrencyValues currency;
	private String fullname;
	private String firstname;
	private String surname;
	private byte[] photo;
	private byte[] photoSmall;

	public static UserDataUserEntity fromJson(UserJson json) {
		UserDataUserEntity user = new UserDataUserEntity();
		user.setId(json.id());
		user.setUsername(json.username());
		user.setCurrency(json.currency());
		user.setFullname(json.fullname());
		user.setFirstname(json.firstname());
		user.setSurname(json.surname());
		user.setPhoto(json.photo().getBytes(StandardCharsets.UTF_8));
		user.setPhotoSmall(json.photoSmall().getBytes(StandardCharsets.UTF_8));
		return user;
	}
}