package de.fraunhofer.iao.querimonia.utility;

import org.eclipse.persistence.oxm.annotations.XmlPath;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XmlMapAdapter extends XmlAdapter<XmlMapAdapter.AdaptedMap, Map<String, Double>> {

  public static class AdaptedMap {
    @XmlPath("class/")
    public List<Entry> entryList = new ArrayList<>();
  }

  public static class Entry {
    public Entry(String value, Double confidence) {
      this.value = value;
      this.confidence = confidence;
    }

    private Entry() {
    }

    @XmlValue()
    public String value;
    @XmlAttribute()
    public Double confidence;

  }

  @Override
  public Map<String, Double> unmarshal(AdaptedMap adaptedMap) {
    Map<String, Double> unmarshaledMap = new HashMap<>();
    for (Entry entry : adaptedMap.entryList) {
      unmarshaledMap.put(entry.value, entry.confidence);
    }
    return unmarshaledMap;
  }

  @Override
  public AdaptedMap marshal(Map<String, Double> stringDoubleMap) {
    AdaptedMap marshalledMap = new AdaptedMap();
    stringDoubleMap.entrySet().stream()
        .map(entry -> new Entry(entry.getKey(), entry.getValue()))
        .forEach(entry -> marshalledMap.entryList.add(entry));
    return marshalledMap;
  }


}
