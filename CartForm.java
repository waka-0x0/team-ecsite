package jp.co.internous.panama.model.form;

import java.io.Serializable;

/**
 * カートフォーム
 * @author waka-0x0
 *
 */
public class CartForm implements Serializable {
	private static final long serialVersionUID = 1L;

	private int userId;
	private int productId;
	private int productCount;

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getProductId() {
		return productId;
	}

	public void setProductId(int productId) {
		this.productId = productId;
	}

	public int getProductCount() {
		return productCount;
	}

	public void setProductCount(int productCount) {
		this.productCount = productCount;
	}

}
