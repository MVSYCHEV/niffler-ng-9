package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.tpl.Connections;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CategoryDaoJdbc implements CategoryDao {
	private static final Config CFG = Config.getInstance();

	@Override
	public CategoryEntity create(CategoryEntity category) {
		try (PreparedStatement ps = Connections.holder(CFG.spendJdbcUrl()).connection().prepareStatement(
				"INSERT INTO category (username, name, archived) " +
						"VALUES (?, ?, ?)",
				Statement.RETURN_GENERATED_KEYS
		)) {
			ps.setString(1, category.getUsername());
			ps.setString(2, category.getName());
			ps.setBoolean(3, category.isArchived());

			ps.executeUpdate();

			final UUID generatedKey;
			try (ResultSet rs = ps.getGeneratedKeys()) {
				if (rs.next()) {
					generatedKey = rs.getObject("id", UUID.class);
				} else {
					throw new SQLException("Can`t find id in ResultSet");
				}
			}
			category.setId(generatedKey);
			return category;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Optional<CategoryEntity> findById(UUID id) {
		try (PreparedStatement ps = Connections.holder(CFG.spendJdbcUrl()).connection().prepareStatement(
				"SELECT * FROM category WHERE id = ?"
		)) {
			ps.setObject(1, id);
			ps.execute();
			try (ResultSet rs = ps.getResultSet()) {
				if (rs.next()) {
					CategoryEntity ce = new CategoryEntity();
					ce.setId(rs.getObject("id", UUID.class));
					ce.setUsername(rs.getString("username"));
					ce.setName(rs.getString("name"));
					ce.setArchived(rs.getBoolean("archived"));
					return Optional.of(ce);
				} else {
					return Optional.empty();
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Optional<CategoryEntity> findByUsernameAndCategoryName(String username, String categoryName) {
		try (PreparedStatement ps = Connections.holder(CFG.spendJdbcUrl()).connection().prepareStatement(
				"SELECT * FROM category WHERE username = ? AND name = ?"
		)) {
			ps.setObject(1, username);
			ps.setObject(2, categoryName);
			ps.execute();
			try (ResultSet rs = ps.getResultSet()) {
				if (rs.next()) {
					CategoryEntity ce = new CategoryEntity();
					ce.setId(rs.getObject("id", UUID.class));
					ce.setUsername(rs.getString("username"));
					ce.setName(rs.getString("name"));
					ce.setArchived(rs.getBoolean("archived"));
					return Optional.of(ce);
				} else {
					return Optional.empty();
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<CategoryEntity> findAllByUsername(String username) {
		List<CategoryEntity> allCategory = new ArrayList<>();
		try (PreparedStatement ps = Connections.holder(CFG.spendJdbcUrl()).connection().prepareStatement(
				"SELECT * FROM category WHERE username = ?"
		)) {
			ps.setObject(1, username);
			ps.execute();
			try (ResultSet rs = ps.getResultSet()) {
				while (rs.next()) {
					CategoryEntity ce = new CategoryEntity();
					ce.setId(rs.getObject("id", UUID.class));
					ce.setUsername(rs.getString("username"));
					ce.setName(rs.getString("name"));
					ce.setArchived(rs.getBoolean("archived"));
					allCategory.add(ce);
				}
			}
			return allCategory;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<CategoryEntity> findAll() {
		List<CategoryEntity> allCategories = new ArrayList<>();
		try (PreparedStatement ps = Connections.holder(CFG.spendJdbcUrl()).connection().prepareStatement(
				"SELECT * FROM category"
		)) {
			ps.execute();
			try (ResultSet rs = ps.getResultSet()) {
				while (rs.next()) {
					CategoryEntity categoryEntity = new CategoryEntity();
					categoryEntity.setId(rs.getObject("id", UUID.class));
					categoryEntity.setUsername(rs.getString("username"));
					categoryEntity.setName(rs.getString("name"));
					categoryEntity.setArchived(rs.getBoolean("archived"));
					allCategories.add(categoryEntity);
				}
			}
			return allCategories;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void deleteCategory(CategoryEntity category) {
		try (PreparedStatement ps = Connections.holder(CFG.spendJdbcUrl()).connection().prepareStatement(
				"DELETE FROM category WHERE id = ?"
		)) {
			ps.setObject(1, category.getId());
			int deleteRows = ps.executeUpdate();
			if (deleteRows == 0) {
				throw new SQLException("Deleting category failed, no rows was deleted.");
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
