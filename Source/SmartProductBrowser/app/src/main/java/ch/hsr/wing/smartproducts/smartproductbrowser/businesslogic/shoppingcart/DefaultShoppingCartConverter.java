package ch.hsr.wing.smartproducts.smartproductbrowser.businesslogic.shoppingcart;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;

import ch.hsr.wing.smartproducts.smartproductbrowser.dataaccess.local.IProductRepository;
import ch.hsr.wing.smartproducts.smartproductbrowser.dataaccess.local.entities.Product;
import ch.hsr.wing.smartproducts.smartproductbrowser.dataaccess.remote.entities.DataDto;
import ch.hsr.wing.smartproducts.smartproductbrowser.entities.CartItem;
import ch.hsr.wing.smartproducts.smartproductbrowser.entities.ShoppingCart;

public class DefaultShoppingCartConverter implements IDataDtoConverter {

    private final IProductRepository _repo;

    @Inject
    public DefaultShoppingCartConverter(IProductRepository repo){
        this._repo = repo;
    }

    @Override
    public ShoppingCart toShoppingCart(DataDto data) {
        Iterable<CartItem> items = this.toItems(data.getData());
        return new ShoppingCart(items, data.getTimestamp());
    }

    private Iterable<CartItem> toItems(JsonObject data) {
        List<CartItem> items = new ArrayList<>();
        for(Map.Entry<String, JsonElement> entry : data.entrySet()){
            try {
                UUID id = UUID.fromString(entry.getKey());
                int amount = entry.getValue().getAsInt();
                Product p = this._repo.get(id);
                items.add(new CartItem(id, amount, p));
            } catch (Throwable t) {
                continue;
            }
        }
        return items;
    }
}