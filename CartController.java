package jp.co.internous.panama.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;

import jp.co.internous.panama.model.domain.TblCart;
import jp.co.internous.panama.model.domain.dto.CartDto;
import jp.co.internous.panama.model.form.CartForm;
import jp.co.internous.panama.model.mapper.TblCartMapper;
import jp.co.internous.panama.model.session.LoginSession;

/**
 * カート情報に関する処理のコントローラー
 * @author waka-0x0
 *
 */
@Controller
@RequestMapping("/panama/cart")
public class CartController {

	/*
	 * フィールド定義
	 */
	@Autowired
	private TblCartMapper cartMapper;

	@Autowired
	private LoginSession loginSession;

	private Gson gson = new Gson();

	/**
	 * カート画面を初期表示する。
	 * @param m 画面表示用オブジェクト
	 * @return カート画面
	 */
	@RequestMapping("/")
	public String index(Model m) {

		// ユーザー(ログイン時：ユーザーID,未ログイン時：仮ユーザーID)に紐づくカート情報のみ取得
		if (loginSession.isLogined()) {
			int userId = loginSession.getUserId();
			List<CartDto> carts = cartMapper.findByUserId(userId);
			m.addAttribute("carts", carts);
		} else {
			int tmpUserId = loginSession.getTmpUserId();
			List<CartDto> carts = cartMapper.findByUserId(tmpUserId);
			m.addAttribute("carts", carts);
		}
		m.addAttribute("loginSession", loginSession);

		return "cart";
	}

	/**
	 * カートに追加処理を行う
	 * @param f カート情報のForm
	 * @param m 画面表示用オブジェクト
	 * @return カート画面
	 */
	@RequestMapping("/add")
	public String addCart(CartForm f, Model m) {

		// ユーザーIDを取得
		int userId = loginSession.isLogined() ? loginSession.getUserId() : loginSession.getTmpUserId();

		f.setUserId(userId);

		TblCart cart = new TblCart(f);
		
		// ユーザーに紐づくカート情報に、追加する商品IDと一致するデータが存在するかチェック
		if (cartMapper.findCountByUserIdAndProuductId(userId, f.getProductId()) > 0) {
			cartMapper.update(cart);
		} else {
			cartMapper.insert(cart);
		}
		
		List<CartDto> carts = cartMapper.findByUserId(userId);
		
		m.addAttribute("carts", carts);
		m.addAttribute("loginSession", loginSession);

		return "cart";
	}

	/**
	 * カート情報を削除する
	 * @param checkedIdList 選択したカート情報のIDリスト
	 * @return true:削除成功、false:削除失敗
	 */
	@SuppressWarnings("unchecked")
	@PostMapping("/delete")
	@ResponseBody
	public boolean deleteCart(@RequestBody String checkedIdList) {

		int deleteCount = 0;
		// 画面から渡されたcheckedIdListを取得
		Map<String, List<Integer>> map = gson.fromJson(checkedIdList, Map.class);
		List<Integer> checkedIds = map.get("checkedIdList");

		deleteCount = cartMapper.deleteById(checkedIds);
		// 削除された行の数が、削除する対象の要素数と一致している場合に成功(true)と判断
		return deleteCount == checkedIds.size();
	}

}
