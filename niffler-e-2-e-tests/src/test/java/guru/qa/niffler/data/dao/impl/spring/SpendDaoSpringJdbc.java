package guru.qa.niffler.data.dao.impl.spring;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.mapper.SpendEntityRowMapper;
import guru.qa.niffler.data.tpl.DataSources;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class SpendDaoSpringJdbc implements SpendDao {
	private static final Config CFG = Config.getInstance();

	@Override
	@Nonnull
	public SpendEntity create(SpendEntity spend) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));
		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(
				connection -> {
					PreparedStatement ps = connection.prepareStatement(
							"INSERT INTO spend (username, spend_date, currency, amount, description, category_id) " +
									"VALUES ( ?, ?, ?, ?, ?, ?)",
							Statement.RETURN_GENERATED_KEYS
					);
					ps.setString(1, spend.getUsername());
					ps.setDate(2, new java.sql.Date(spend.getSpendDate().getTime()));
					ps.setString(3, spend.getCurrency().name());
					ps.setDouble(4, spend.getAmount());
					ps.setString(5, spend.getDescription());
					ps.setObject(6, spend.getCategory().getId());
					return ps;
				},
				keyHolder
		);
		final UUID generatedKey = (UUID) keyHolder.getKeys().get("id");
		spend.setId(generatedKey);
		return spend;
	}

	@Override
	@Nonnull
	public SpendEntity update(SpendEntity spend) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));
		jdbcTemplate.update("UPDATE spend SET spend_date = ?, currency = ?, amount = ?, description = ? WHERE id = ?",
				new Date(spend.getSpendDate().getTime()),
				spend.getCurrency().name(),
				spend.getAmount(),
				spend.getDescription(),
				spend.getId()
		);
		return spend;
	}

	@Override
	@Nonnull
	public Optional<SpendEntity> findSpendById(UUID id) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));
		try {
			SpendEntity spendEntity = jdbcTemplate.queryForObject(
					"SELECT s.*, c.* " +
							"FROM spend s " +
							"JOIN category c ON s.category_id = c.id " +
							"WHERE s.id = ?",
					SpendEntityRowMapper.instance,
					id
			);
			CategoryEntity category = new CategoryEntity();
			category.setId(spendEntity.getCategory().getId());
			spendEntity.setCategory(category);
			return Optional.of(spendEntity);
		} catch (EmptyResultDataAccessException e) {
			return Optional.empty();
		}
	}

	@Override
	@Nonnull
	public List<SpendEntity> findAllByUsername(String username) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));
		List<SpendEntity> spendEntities = jdbcTemplate.query(
				"SELECT * FROM spend WHERE username = ?",
				SpendEntityRowMapper.instance,
				username
		);
		spendEntities.forEach(
				spend -> {
					UUID categoryId = spend.getCategory().getId();
					CategoryEntity category = new CategoryEntity();
					category.setId(categoryId);
					spend.setCategory(category);
				}
		);
		return spendEntities;
	}

	@Override
	@Nonnull
	public Optional<SpendEntity> findByUsernameAndSpendDescription(String username, String description) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));
		try {
			SpendEntity spendEntity = jdbcTemplate.queryForObject(
					"SELECT s.*, c.* " +
							"FROM spend s " +
							"JOIN category c ON s.category_id = c.id " +
							"WHERE s.username = ? AND s.description = ?",
					SpendEntityRowMapper.instance,
					username,
					description
			);
			CategoryEntity category = new CategoryEntity();
			category.setId(spendEntity.getCategory().getId());
			spendEntity.setCategory(category);
			return Optional.of(spendEntity);
		} catch (EmptyResultDataAccessException e) {
			return Optional.empty();
		}
	}

	@Override
	@Nonnull
	public List<SpendEntity> findAll() {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));
		List<SpendEntity> spendEntities = jdbcTemplate.query(
				"SELECT * FROM spend",
				SpendEntityRowMapper.instance
		);
		spendEntities.forEach(
				spend -> {
					UUID categoryId = spend.getCategory().getId();
					CategoryEntity category = new CategoryEntity();
					category.setId(categoryId);
					spend.setCategory(category);
				}
		);
		return spendEntities;
	}

	@Override
	public void deleteSpend(SpendEntity spend) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));
		jdbcTemplate.update(
				"DELETE FROM spend WHERE id = ?",
				spend.getId()
		);
	}
}
