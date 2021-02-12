package mnykolaichuk.prz.pracaDyplomowa.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.sql.Timestamp;
import java.time.LocalDate;

@Converter(autoApply = true)
public class LocalDateAttributeConverter implements
        AttributeConverter<LocalDate, Timestamp> {

    @Override
    public Timestamp convertToDatabaseColumn(LocalDate localDate) {
        if(localDate == null)
            return null;
        return Timestamp.valueOf(localDate.atStartOfDay());
    }

    @Override
    public LocalDate convertToEntityAttribute(Timestamp timestamp) {
        if(timestamp == null)
            return null;
        return timestamp.toLocalDateTime().toLocalDate();
    }
}