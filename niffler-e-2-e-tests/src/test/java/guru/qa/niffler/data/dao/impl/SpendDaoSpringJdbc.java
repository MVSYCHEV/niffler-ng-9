package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.mapper.SpendEntityRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SpendDaoSpringJdbc implements SpendDao {
	private final DataSource dataSource;

	public SpendDaoSpringJdbc(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public SpendEntity create(SpendEntity spend) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(
				connection -> {
					PreparedStatement ps = connection.prepareStatement(
							"INSERT INTO spend (username, spend_date, currency, amount, description, category_id) " +
									"VALUES ( ?, ?, ?, ?, ?, ?)",
							Statement.RETURN_GENERATED_KEYS
					);
					ps.setString(1, spend.getUsername());
					ps.setDate(2, spend.getSpendDate());
					ps.setString(3, spend.getCurrency().name());
					ps.setDouble(4, spend.getAmount());
					ps.setString(5, spend.getDescription());
					ps.setObject(6, spend.getCategoryId());
					return ps;
				},
				keyHolder
		);
		final UUID generatedKey = (UUID) keyHolder.getKeys().get("id");
		spend.setId(generatedKey);
		return spend;
	}

	@Override
	public Optional<SpendEntity> findSpendById(UUID id) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		SpendEntity spendEntity = jdbcTemplate.queryForObject(
						"SELECT s.*, c.* " +
								"FROM spend s " +
								"JOIN category c ON s.category_id = c.id " +
								"WHERE s.id = ?",
						SpendEntityRowMapper.instance,
						id
				);
		CategoryEntity category = new CategoryEntity();
		category.setId(spendEntity.getCategoryId());
		spendEntity.setCategory(category);
		return Optional.ofNullable(spendEntity);
	}

	@Override
	public List<SpendEntity> findAllByUsername(String username) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		List<SpendEntity> spendEntities = jdbcTemplate.query(
				"SELECT * FROM spend WHERE username = ?",
				SpendEntityRowMapper.instance,
				username
		);
		spendEntities.forEach(
				spend -> {
					UUID categoryId = spend.getCategoryId();
					CategoryEntity category = new CategoryEntity();
					category.setId(categoryId);
					spend.setCategory(category);
				}
		);
		return spendEntities;
	}

	@Override
	public List<SpendEntity> findAll() {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		List<SpendEntity> spendEntities = jdbcTemplate.query(
				"SELECT * FROM spend",
				SpendEntityRowMapper.instance
		);
		spendEntities.forEach(
				spend -> {
					UUID categoryId = spend.getCategoryId();
					CategoryEntity category = new CategoryEntity();
					category.setId(categoryId);
					spend.setCategory(category);
				}
		);
		return spendEntities;
	}

	@Override
	public void deleteSpend(SpendEntity spend) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		jdbcTemplate.update(
				"DELETE FROM spend WHERE id = ?",
				spend.getId()
		);
	}
}
