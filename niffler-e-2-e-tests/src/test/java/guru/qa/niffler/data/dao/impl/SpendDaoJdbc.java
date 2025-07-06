package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.model.CurrencyValues;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SpendDaoJdbc implements SpendDao {
	private final Connection connection;

	public SpendDaoJdbc(Connection connection) {
		this.connection = connection;
	}

	@Override
	public SpendEntity create(SpendEntity spend) {
		try (PreparedStatement ps = connection.prepareStatement(
				"INSERT INTO spend (username, spend_date, currency, amount, description, category_id) " +
						"VALUES ( ?, ?, ?, ?, ?, ?)",
				Statement.RETURN_GENERATED_KEYS
		)) {
			ps.setString(1, spend.getUsername());
			ps.setDate(2, spend.getSpendDate());
			ps.setString(3, spend.getCurrency().name());
			ps.setDouble(4, spend.getAmount());
			ps.setString(5, spend.getDescription());
			ps.setObject(6, spend.getCategory().getId());

			ps.executeUpdate();

			final UUID generatedKey;
			try (ResultSet rs = ps.getGeneratedKeys()) {
				if (rs.next()) {
					generatedKey = rs.getObject("id", UUID.class);
				} else {
					throw new SQLException("Can`t find id in ResultSet");
				}
			}
			spend.setId(generatedKey);
			return spend;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Optional<SpendEntity> findSpendById(UUID id) {
		try (PreparedStatement ps = connection.prepareStatement(
				"SELECT s.*, c.* " +
						"FROM spend s " +
						"JOIN category c ON s.category_id = c.id " +
						"WHERE s.id = ?"
		)) {
			ps.setObject(1, id);
			ps.execute();
			try (ResultSet resultSet = ps.getResultSet()) {
				if (resultSet.next()) {
					SpendEntity spendEntity = new SpendEntity();
					spendEntity.setId(id);
					spendEntity.setUsername(resultSet.getString("username"));

					String currencyStr = resultSet.getString("currency");
					CurrencyValues currency = CurrencyValues.valueOf(currencyStr);
					spendEntity.setCurrency(currency);

					spendEntity.setAmount(resultSet.getDouble("amount"));
					spendEntity.setDescription(resultSet.getString("description"));
					spendEntity.setSpendDate(resultSet.getDate("spend_date"));

					UUID categoryId = resultSet.getObject("category_id", UUID.class);
					Optional<CategoryEntity> category = new CategoryDaoJdbc(connection).findCategoryById(categoryId);
					if (category.isPresent()) {
						spendEntity.setCategory(category.get());
					}
					return Optional.of(spendEntity);
				} else {
					return Optional.empty();
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<SpendEntity> findAllByUsername(String username) {
		List<SpendEntity> allSpends = new ArrayList<>();
		try (PreparedStatement ps = connection.prepareStatement(
				"SELECT * FROM spend WHERE username = ?"
		)) {
			ps.setObject(1, username);
			ps.execute();
			try (ResultSet resultSet = ps.getResultSet()) {
				while (resultSet.next()) {
					SpendEntity spendEntity = new SpendEntity();
					spendEntity.setId(resultSet.getObject("id", UUID.class));
					spendEntity.setUsername(resultSet.getString("username"));

					String currencyStr = resultSet.getString("currency");
					CurrencyValues currency = CurrencyValues.valueOf(currencyStr);
					spendEntity.setCurrency(currency);

					spendEntity.setAmount(resultSet.getDouble("amount"));
					spendEntity.setDescription(resultSet.getString("description"));
					spendEntity.setSpendDate(resultSet.getDate("spend_date"));

					UUID categoryId = resultSet.getObject("category_id", UUID.class);
					Optional<CategoryEntity> category = new CategoryDaoJdbc(connection).findCategoryById(categoryId);
					if (category.isPresent()) {
						spendEntity.setCategory(category.get());
					}
					allSpends.add(spendEntity);
				}
			}
			return allSpends;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void deleteSpend(SpendEntity spend) {
		try (PreparedStatement ps = connection.prepareStatement(
				"DELETE FROM spend WHERE id = ?"
		)) {
			ps.setObject(1, spend.getId());
			int deleteRows = ps.executeUpdate();
			if (deleteRows == 0) {
				throw new SQLException("Deleting category failed, no rows was deleted.");
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
