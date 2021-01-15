package ru.qiwi.devops.mission.control.utils.logging

import ch.qos.logback.classic.spi.ILoggingEvent
import com.fasterxml.jackson.core.JsonGenerator
import net.logstash.logback.composite.AbstractFieldJsonProvider
import net.logstash.logback.composite.FieldNamesAware
import net.logstash.logback.fieldnames.LogstashFieldNames

class WholeMdcProvider : AbstractFieldJsonProvider<ILoggingEvent>(), FieldNamesAware<LogstashFieldNames> {
    init {
        this.fieldName = "mdc"
    }

    override fun setFieldNames(fieldNames: LogstashFieldNames) {
        this.fieldName = fieldNames.tags
    }

    override fun writeTo(generator: JsonGenerator, event: ILoggingEvent) {
        val mdc = event.mdcPropertyMap
        if (mdc.isNotEmpty()) {
            generator.writeObjectField(fieldName, mdc)
        }
    }
}
