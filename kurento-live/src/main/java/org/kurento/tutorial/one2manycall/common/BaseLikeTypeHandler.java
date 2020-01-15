package org.kurento.tutorial.one2manycall.common;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

public class BaseLikeTypeHandler extends BaseTypeHandler<CharSequence> {
  private static final String LIKE = "%";
  private final boolean leftLike;
  private final boolean rightLike;

  public BaseLikeTypeHandler(boolean leftLike, boolean rightLike) {
    this.leftLike = leftLike;
    this.rightLike = rightLike;
  }

  public static String likeConvert(String value) {
    if (StringUtils.isNotBlank(value)) {
      value = value.replaceAll("%", "\\\\%");
      value = value.replaceAll("_", "\\\\_");
      return value;
    } else {
      return "";
    }
  }

  public static String likeConvertProcess(String value) {
    if (StringUtils.isNotBlank(value)) {
      value = value.replaceAll("%", "\\\\%");
      value = value.replaceAll("_", "\\\\_");
      return "%" + value + "%";
    } else {
      return "";
    }
  }

  public static String likeConvert(Object value) {
    return value instanceof String ? likeConvert(String.valueOf(value)) : "";
  }

  private String convert(String value) {
    value = value.replaceAll("\\%", "\\\\%");
    value = value.replaceAll("\\_", "\\\\_");
    return value;
  }

  public void setNonNullParameter(PreparedStatement ps, int i, CharSequence parameter, JdbcType jdbcType) throws SQLException {
    if (parameter == null) {
      ps.setString(i, (String)null);
    } else {
      ps.setString(i, this.like(parameter.toString()));
    }

  }

  private String like(String parameter) {
    String result = this.convert(parameter);
    if (this.leftLike) {
      result = "%" + result;
    }

    if (this.rightLike) {
      result = result + "%";
    }

    return result;
  }

  public String getNullableResult(ResultSet rs, String columnName) throws SQLException {
    return rs.getString(columnName);
  }

  public String getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
    return rs.getString(columnIndex);
  }

  public String getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
    return cs.getString(columnIndex);
  }
}
