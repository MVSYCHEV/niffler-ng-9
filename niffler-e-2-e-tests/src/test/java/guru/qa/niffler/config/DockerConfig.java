package guru.qa.niffler.config;

import javax.annotation.Nonnull;

enum DockerConfig implements Config {
  INSTANCE;

  @Override
  @Nonnull
  public String frontUrl() {
    return "";
  }

  @Override
  @Nonnull
  public String authUrl() {
    return "";
  }

  @Override
  @Nonnull
  public String authJdbcUrl() {
    return "";
  }

  @Override
  @Nonnull
  public String gatewayUrl() {
    return "";
  }

  @Override
  @Nonnull
  public String userdataUrl() {
    return "";
  }

  @Override
  @Nonnull
  public String userdataJdbcUrl() {
    return "";
  }

  @Override
  @Nonnull
  public String spendUrl() {
    return "";
  }

  @Override
  @Nonnull
  public String spendJdbcUrl() {
    return "";
  }

  @Override
  @Nonnull
  public String currencyJdbcUrl() {
    return "";
  }

}
