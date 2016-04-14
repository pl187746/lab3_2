package edu.iis.mto.staticmock;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import edu.iis.mto.staticmock.reader.NewsReader;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ConfigurationLoader.class, NewsReaderFactory.class})
public class NewsLoaderTest {
	
	ConfigurationLoader configurationLoader;
	
	@Before
	public void setUp() {
		mockStatic(ConfigurationLoader.class);
		Configuration configuration = new Configuration();
		configurationLoader = mock(ConfigurationLoader.class);
		when(configurationLoader.loadConfiguration()).thenReturn(configuration);
		when(ConfigurationLoader.getInstance()).thenReturn(configurationLoader);
	}
	
	@Test
	public void newsyDoOpublikowaniaSaPubliczne() {
		PublishableNews publishableNews = performTest(SubsciptionType.NONE);
		assertThat(publishableNews.getPublicContent().isEmpty(), is(false));
		assertThat(publishableNews.getSubscribentContent().isEmpty(), is(true));
	}
	
	@Test
	public void newsyDlaSubskrybentowSaDostepneTylkoDlaSubskrybentow() {
		PublishableNews publishableNews = performTest(SubsciptionType.A);
		assertThat(publishableNews.getPublicContent().isEmpty(), is(true));
		assertThat(publishableNews.getSubscribentContent().isEmpty(), is(false));
	}
	
	@Test
	public void newsLoaderPobieraKonfiguracje() {
		performTest(SubsciptionType.NONE);
		verify(configurationLoader).loadConfiguration();
	}
	
	private PublishableNews performTest(SubsciptionType type) {
		prepareIncomingNews(type);
		NewsLoader newsLoader = new NewsLoader();
		PublishableNews publishableNews = newsLoader.loadNews();
		return publishableNews;
	}
	
	private void prepareIncomingNews(SubsciptionType type) {
		mockNewsReaderFactory(mockNewsReader(createIncomingNews(type)));
	}
	
	private void mockNewsReaderFactory(NewsReader newsReader) {
		mockStatic(NewsReaderFactory.class);
		when(NewsReaderFactory.getReader(any(String.class))).thenReturn(newsReader);
	}
	
	private NewsReader mockNewsReader(IncomingNews incomingNews) {
		NewsReader newsReader = mock(NewsReader.class);
		when(newsReader.read()).thenReturn(incomingNews);
		return newsReader;
	}
	
	private IncomingNews createIncomingNews(SubsciptionType type) {
		IncomingNews incomingNews = new IncomingNews();
		incomingNews.add(new IncomingInfo("Hello world!", type));
		return incomingNews;
	}

}
