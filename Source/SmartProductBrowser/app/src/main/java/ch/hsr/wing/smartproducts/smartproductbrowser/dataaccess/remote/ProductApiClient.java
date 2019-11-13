package ch.hsr.wing.smartproducts.smartproductbrowser.dataaccess.remote;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import ch.hsr.wing.smartproducts.smartproductbrowser.dataaccess.remote.entities.ContentResponse;
import ch.hsr.wing.smartproducts.smartproductbrowser.dataaccess.remote.entities.ProductDto;
import ch.hsr.wing.smartproducts.smartproductbrowser.dataaccess.remote.entities.ProductInfoDto;
import ch.hsr.wing.smartproducts.smartproductbrowser.dataaccess.remote.entities.ResponseTypes;
import ch.hsr.wing.smartproducts.smartproductbrowser.util.settings.IConnectionSettings;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class ProductApiClient implements IProductApiClient {

    private final IConnectionSettings _settings;
    private final OkHttpClient _client;

    @Inject
    public ProductApiClient(IConnectionSettings settings, OkHttpClient client){
        this._settings = settings;
        this._client = client;
    }

    private static final String API_URL = "api";

    private HttpUrl.Builder getApi(){
        return HttpUrl.parse(this._settings.getProductsEndpoint()).newBuilder().addPathSegment(API_URL);
    }

    private static final String PING_URL = "ping";
    @Override
    public ResponseTypes ping() {
        try {
            HttpUrl.Builder url = this.getApi().addPathSegment(PING_URL);
            Request request = new Request.Builder().url(url.toString()).build();
            try (Response response = this._client.newCall(request).execute()) {
                if(response.isSuccessful()){
                    return ResponseTypes.OK;
                }
                return HttpUtil.toResponseType(response.code());
            }
        } catch (Exception ex){
            return ResponseTypes.UNREACHABLE;
        }
    }

    private static final String ALL_PRODUCTS = "products";
    @Override
    public ContentResponse<List<ProductInfoDto>> getAllProducts() {
        try{
            HttpUrl.Builder url = this.getApi().addPathSegment(ALL_PRODUCTS);
            Request request = new Request.Builder().url(url.toString()).build();
            try(Response response = this._client.newCall(request).execute()){
                if(!response.isSuccessful()){
                    return HttpUtil.noSuccess(response);
                }
                ProductInfoDto[] dtos = HttpUtil.parseJsonTo(ProductInfoDto[].class, response.body());
                return new ContentResponse<>(ResponseTypes.OK, Arrays.asList(dtos));
            }
        } catch (JsonParseException ex) {
            return new ContentResponse<>(ResponseTypes.BAD_RESPONSE);
        } catch (Exception ex){
            return new ContentResponse<>(ResponseTypes.UNREACHABLE);
        }
    }

    private static final String PRODUCT_BY_ID = "products";
    @Override
    public ContentResponse<ProductDto> getDetailsOfProductById(UUID productId) {
        try{
            HttpUrl.Builder url = this.getApi().addPathSegment(PRODUCT_BY_ID)
                    .addPathSegment(productId.toString());
            Request request = new Request.Builder().url(url.toString()).build();
            try(Response response = this._client.newCall(request).execute()){
                if(!response.isSuccessful()){
                    return HttpUtil.noSuccess(response);
                }
                ProductDto dto = HttpUtil.parseJsonTo(ProductDto.class, response.body());
                return new ContentResponse<>(ResponseTypes.OK, dto);
            }
        } catch (JsonParseException ex) {
            return new ContentResponse<>(ResponseTypes.BAD_RESPONSE);
        } catch (Exception ex){
            return new ContentResponse<>(ResponseTypes.UNREACHABLE);
        }
    }
}
