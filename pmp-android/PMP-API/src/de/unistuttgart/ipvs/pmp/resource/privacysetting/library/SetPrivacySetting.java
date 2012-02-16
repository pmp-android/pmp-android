package de.unistuttgart.ipvs.pmp.resource.privacysetting.library;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.HashSet;
import java.util.Set;

import android.content.Context;
import de.unistuttgart.ipvs.pmp.resource.privacysetting.AbstractPrivacySetting;
import de.unistuttgart.ipvs.pmp.resource.privacysetting.IPrivacySettingView;
import de.unistuttgart.ipvs.pmp.resource.privacysetting.IStringConverter;
import de.unistuttgart.ipvs.pmp.resource.privacysetting.PrivacySettingValueException;
import de.unistuttgart.ipvs.pmp.resource.privacysetting.view.SetView;

/**
 * Privacy setting for set types.
 * 
 * @author Tobias Kuhn, Jakob Jarosch
 * 
 * @param <T>
 *            the {@link Serializable} type to be stored
 */
public class SetPrivacySetting<T> extends AbstractPrivacySetting<Set<T>> {
    
    private static final String SEPARATOR = ";";
    private static final String SEPARATOR_REGEX = "\\;";
    private static final String ESCAPE_SEPARATOR = "\\;";
    
    private IStringConverter<T> converter;
    
    private Constructor<? extends IPrivacySettingView<T>> childViewConstructor;
    private Object[] childViewConstructorInvocation;
    
    
    public SetPrivacySetting(IStringConverter<T> converter,
            Constructor<? extends IPrivacySettingView<T>> childViewConstructor,
            Object... childViewConstructorInvocation) {
        super();
        this.converter = converter;
        this.childViewConstructor = childViewConstructor;
        this.childViewConstructorInvocation = childViewConstructorInvocation;
    }
    
    
    @Override
    public Set<T> parseValue(String value) throws PrivacySettingValueException {
        if ((value == null) || value.equals("")) {
            return new HashSet<T>();
        }
        
        Set<T> set = new HashSet<T>();
        for (String item : value.split(SEPARATOR_REGEX)) {
            set.add(this.converter.valueOf(item.replace(ESCAPE_SEPARATOR, SEPARATOR)));
        }
        
        return set;
    }
    
    
    @Override
    public String valueToString(Set<T> value) {
        if (value == null) {
            return null;
        }
        
        StringBuilder sb = new StringBuilder();
        for (T item : value) {
            sb.append(this.converter.toString(item).replace(SEPARATOR, ESCAPE_SEPARATOR));
            sb.append(SEPARATOR);
        }
        return sb.toString();
        
    }
    
    
    @Override
    public boolean permits(Set<T> value, Set<T> reference) {
        return value.containsAll(reference);
    }
    
    
    @Override
    public String getHumanReadableValue(String value) throws PrivacySettingValueException {
        Set<T> set = parseValue(value);
        if (set.isEmpty()) {
            return "";
        }
        
        StringBuilder sb = new StringBuilder();
        for (T item : set) {
            sb.append(item.toString());
            sb.append(", ");
        }
        return sb.substring(0, sb.length() - 2);
    }
    
    
    @Override
    public IPrivacySettingView<Set<T>> makeView(Context context) {
        return new SetView<T>(context, this.childViewConstructor, this.childViewConstructorInvocation);
    }
    
}
