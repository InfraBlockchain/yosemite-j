package io.yosemite.data.util;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import io.yosemite.data.remote.chain.TransactionExtension;
import io.yosemite.util.StringUtils;

import java.io.IOException;

public class TransactionExtensionTypeAdapter extends TypeAdapter<TransactionExtension> {
    @Override
    public void write(JsonWriter out, TransactionExtension value) throws IOException {
        out.beginArray();
        out.value(value.getField());
        out.value(StringUtils.convertByteArrayToHexString(value.getData()));
        out.endArray();
    }

    @Override
    public TransactionExtension read(JsonReader in) throws IOException {
        in.beginArray();
        short field = (short) in.nextInt();
        String nextString = in.nextString();
        TransactionExtension transactionExtension = new TransactionExtension(
                field, StringUtils.convertHexStringToByteArray(nextString));
        in.endArray();
        return transactionExtension;
    }
}
