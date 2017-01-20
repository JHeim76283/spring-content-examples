package examples;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;

import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.github.paulcwarren.ginkgo4j.Ginkgo4jSpringRunner;

@RunWith(Ginkgo4jSpringRunner.class)
@SpringBootTest
public class SolrTest {

	@Autowired private DocumentRepository docRepo;
	@Autowired private DocumentContentRepository docContentRepo;
	
	@Autowired private SolrClient solr;
	
	private Document doc;

	private String id = null;
	
	{
		Describe("Solr Examples", () -> {
			Context("given a document", () -> {
				BeforeEach(() -> {
					doc = new Document();
					doc.setTitle("title of document 1");
					doc.setAuthor("author@email.com");
					docContentRepo.setContent(doc, this.getClass().getResourceAsStream("/one.docx"));
					docRepo.save(doc);
				});
				AfterEach(() -> {
					docContentRepo.unsetContent(doc);
					
					docRepo.delete(doc);
				});
				It("should index the content of that document", () -> {
					SolrQuery query = new SolrQuery();
					query.setQuery("one");
					query.addFilterQuery("id:" + doc.getContentId().toString());
					query.setFields("content");
					
					QueryResponse response = solr.query(query);
					SolrDocumentList results = response.getResults();
					
					assertThat(results.size(), is(not(nullValue())));
					assertThat(results.size(), is(1));
				});
                Context("when the content is searched", () -> {
                    It("should return the searched content", () -> {
                        Iterable<Integer> content = docContentRepo.findKeyword("one");
                        assertThat(content, hasItem(doc.getContentId()));
                    });
                });
				Context("given that documents content is updated", () -> {
					BeforeEach(() -> {
						docContentRepo.setContent(doc, this.getClass().getResourceAsStream("/two.rtf"));
						docRepo.save(doc);
					});
					It("should index the new content", () -> {
						SolrQuery query2 = new SolrQuery();
						query2.setQuery("two");
						query2.addFilterQuery("id:" + doc.getContentId().toString());
						query2.setFields("content");
						
						QueryResponse response = solr.query(query2);
						SolrDocumentList results = response.getResults();
						
						assertThat(results.size(), is(not(nullValue())));
						assertThat(results.size(), is(1));
					});
				});
				Context("given that document is deleted", () -> {
					BeforeEach(() -> {
						id = doc.getContentId().toString();
						docContentRepo.unsetContent(doc);
						docRepo.delete(doc);
					});
					It("should delete the record of the content from the index", () -> {
						SolrQuery query = new SolrQuery();
						query.setQuery("one");
						query.addFilterQuery("id:" + id);
						query.setFields("content");

						QueryResponse response = solr.query(query);
						SolrDocumentList results = response.getResults();

						assertThat(results.size(), is(0));
					});
				});
			});
		});
	}
	
	@Test
	public void noop() {
	}
}