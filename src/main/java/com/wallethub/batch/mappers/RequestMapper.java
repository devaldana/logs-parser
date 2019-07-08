package com.wallethub.batch.mappers;

import com.wallethub.domain.Request;
import com.wallethub.util.Util;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;

import static com.wallethub.util.Global.SPACE_SEPARATOR;
import static java.util.Objects.isNull;

/**
 * @author David Aldana
 * @since 2018.07
 */
public class RequestMapper implements FieldSetMapper<Request> {

    @Override
    public Request mapFieldSet(final FieldSet fieldSet) {
        if(isNull(fieldSet)) return null;
        final Request request = new Request();
        request.setDate(Util.parseStringToLocalDateTime(fieldSet.readString(0), SPACE_SEPARATOR));
        request.setIp(fieldSet.readString(1));
        request.setResource(fieldSet.readString(2));
        request.setStatus(fieldSet.readString(3));
        request.setUserAgent(fieldSet.readString(4));
        return request;
    }
}
