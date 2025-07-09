package guru.qa.niffler.data.mapper;

import guru.qa.niffler.data.entity.userdata.UserDataUserEntity;
import guru.qa.niffler.model.spend.CurrencyValues;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class UserDataUserEntityRowMapper implements RowMapper<UserDataUserEntity> {
	public static final UserDataUserEntityRowMapper instance = new UserDataUserEntityRowMapper();

	private UserDataUserEntityRowMapper() {
	}

	@Override
	public UserDataUserEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
		UserDataUserEntity result = new UserDataUserEntity();
		result.setId(rs.getObject("id", UUID.class));
		result.setUsername(rs.getString("username"));
		result.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
		result.setFirstname(rs.getString("firstname"));
		result.setSurname(rs.getString("surname"));
		result.setFullname(rs.getString("full_name"));
		result.setPhoto(rs.getBytes("photo"));
		result.setPhotoSmall(rs.getBytes("photo_small"));
		return result;
	}
}
