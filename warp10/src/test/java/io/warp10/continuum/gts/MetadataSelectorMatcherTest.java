//
//   Copyright 2019  SenX S.A.S.
//
//   Licensed under the Apache License, Version 2.0 (the "License");
//   you may not use this file except in compliance with the License.
//   You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
//   Unless required by applicable law or agreed to in writing, software
//   distributed under the License is distributed on an "AS IS" BASIS,
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//   See the License for the specific language governing permissions and
//   limitations under the License.
//

package io.warp10.continuum.gts;

import io.warp10.continuum.store.thrift.data.Metadata;
import io.warp10.script.WarpScriptException;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class MetadataSelectorMatcherTest {

  @Test
  public void metaDataMatch() throws WarpScriptException {

    // metadata with classname, labels, attributes. no pitfall.
    Metadata test = new Metadata();
    test.setName("temperature");

    Map labels = new HashMap();
    labels.put("sensor", "23");
    test.setLabels(labels);

    Map attributes = new HashMap();
    attributes.put("room", "A");
    test.setAttributes(attributes);


    MetadataSelectorMatcher x;

    x = new MetadataSelectorMatcher("~.*{toto~tata.*}{attr=yes,attr2~.false.*}");
    Assert.assertFalse(x.MetaDataMatch(test)); // there is no such label or attributes

    x = new MetadataSelectorMatcher("~temp.*{sensor~(23|22),room=B}{}");
    Assert.assertFalse(x.MetaDataMatch(test)); // there is room attributes, but room=A

    x = new MetadataSelectorMatcher("~temp.*{sensor~(23|22),room=B}");
    Assert.assertFalse(x.MetaDataMatch(test));

    // metadata with empty classname.
    Metadata testemptyclassname = new Metadata();
    testemptyclassname.setName("");

    labels = new HashMap();
    labels.put("sensor", "23");
    testemptyclassname.setLabels(labels);

    attributes = new HashMap();
    attributes.put("attr", "yes");
    attributes.put("attr2", "afalse detection failure");
    testemptyclassname.setAttributes(attributes);

    // this will have a null name
    GeoTimeSerie g = new GeoTimeSerie();

    x = new MetadataSelectorMatcher("={attr=yes,attr2~.false.*}");
    Assert.assertTrue(x.MetaDataMatch(testemptyclassname));
    Assert.assertFalse(x.MetaDataMatch(test));
    Assert.assertFalse(x.MetaDataMatch(g.getMetadata())); // the empty gts has not these attributes

    x = new MetadataSelectorMatcher("~{attr=yes,attr2~.false.*}");
    Assert.assertTrue(x.MetaDataMatch(testemptyclassname));
    Assert.assertFalse(x.MetaDataMatch(test));

    x = new MetadataSelectorMatcher("={}");
    Assert.assertTrue(x.MetaDataMatch(g.getMetadata()));


  }
}