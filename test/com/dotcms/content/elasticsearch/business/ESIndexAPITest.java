package com.dotcms.content.elasticsearch.business;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.zip.ZipFile;

import org.elasticsearch.ElasticsearchException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.dotmarketing.business.APILocator;
import com.dotmarketing.business.DotStateException;
import com.dotmarketing.exception.DotDataException;
import com.dotmarketing.util.Config;
import com.dotmarketing.util.FileUtil;
import com.dotmarketing.util.IntegrationTestInitService;

public class ESIndexAPITest {

	final private ESIndexAPI esIndexAPI;
	final private ContentletIndexAPI contentletIndexAPI;

    public ESIndexAPITest() {
		this.esIndexAPI = APILocator.getESIndexAPI();
		this.contentletIndexAPI = APILocator.getContentletIndexAPI();
	}

	@BeforeClass
	public static void prepare() throws Exception{
		//Setting web app environment
        IntegrationTestInitService.getInstance().init();
        String pathToRepo = Config.getStringProperty("es.path.repo","test-resources");
		File tempDir = new File(pathToRepo);
		if(!tempDir.exists()){
			tempDir.mkdirs();
		}
		tempDir.deleteOnExit();
	}

	@After
	public void cleanUp(){
		esIndexAPI.deleteRepository("backup");
	}

	/**
	 * Test creation of a snapshot file with a valid index name
	 */
	@Test
	@Ignore
	public void createSnapshotTest(){
		String indexName = getLiveIndex();
		File snapshot = null;
		try {
			snapshot = esIndexAPI.createSnapshot(ESIndexAPI.BACKUP_REPOSITORY, "backup", indexName);
			assertNotNull(snapshot);
		} catch (DotStateException | IllegalArgumentException | IOException e) {
			e.printStackTrace();
		}finally{
			snapshot.delete();
		}
	}

	/**
	 * Creation of a snapshot file using and invalid index, should fail
	 */
	@Test(expected = ElasticsearchException.class)
	@Ignore
	public void createSnapshotTest_invalidIndex(){
		String indexName = "live_xxxxxxxxx";
		File snapshot = null;
		try {
			snapshot = esIndexAPI.createSnapshot(ESIndexAPI.BACKUP_REPOSITORY, "backup", indexName);
			assertNull(snapshot);
		} catch (DotStateException | IllegalArgumentException | IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Downloads current live index, deletes it and restores it
	 * The current live index is removed
	 * The new one is uploaded
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws DotDataException
	 */
	@Test
	public void uploadSnapshotTest_fromCurrentLive() throws IOException, InterruptedException, ExecutionException, DotDataException{
		String indexName = getLiveIndex();
		File snapshot = null;
		try {
			snapshot = esIndexAPI.createSnapshot(ESIndexAPI.BACKUP_REPOSITORY, "backup", indexName);
			esIndexAPI.clearIndex(indexName);
			esIndexAPI.closeIndex(indexName);
			//esIndexAPI.delete(indexName);
			ZipFile file = new ZipFile(snapshot.getAbsolutePath());
			String pathToRepo = Config.getStringProperty("es.path.repo","test-resources");
			File tempDir = new File(pathToRepo);
			boolean response = esIndexAPI.uploadSnapshot(file, tempDir.getAbsolutePath());
			assertTrue(response);
		} catch (DotStateException | IllegalArgumentException | IOException e) {
			e.printStackTrace();
		}finally{
			snapshot.delete();
			esIndexAPI.openIndex(indexName);
			contentletIndexAPI.activateIndex(indexName);
		}
	}

	/**
	 * Uploading a sample index snapshot file, index live_20161011134043 (on a sample zip file)
	 * The current live index is removed
	 * The new one is uploaded
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	@Test
	@Ignore
	public void uploadSnapshotTest() throws IOException, InterruptedException, ExecutionException{
		String currentLiveIndex = getLiveIndex();
		esIndexAPI.closeIndex(currentLiveIndex);
		//esIndexAPI.delete(currentLiveIndex);
		ZipFile file = new ZipFile("test-resources/index.zip");
		String pathToRepo = Config.getStringProperty("es.path.repo","test-resources");
		File tempDir = new File(pathToRepo);
		boolean response = esIndexAPI.uploadSnapshot(file, tempDir.getAbsolutePath());
		assertTrue(response);
	}

	/**
	 * Uploading a sample index snapshot file, should failed as it does not contain
	 * snapshot information
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	@Ignore
	@Test(expected = ElasticsearchException.class)
	public void uploadSnapshotTest_noSnapshotFound() throws IOException, InterruptedException, ExecutionException{
		ZipFile file = new ZipFile("test-resources/failing-test.zip");
		String pathToRepo = Config.getStringProperty("es.path.repo","test-resources");
		File tempDir = new File(pathToRepo);
		esIndexAPI.uploadSnapshot(file, tempDir.getAbsolutePath());
	}

	/**
	 * Download current live index, and restores it, the index already exists and should fail
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	@Ignore
	@Test(expected = ExecutionException.class)
	public void uploadSnapshotTest_alreadyExistingIndex() throws IOException, InterruptedException, ExecutionException{
		String indexName = getLiveIndex();
		File snapshot = null;
		try {
			snapshot = esIndexAPI.createSnapshot(ESIndexAPI.BACKUP_REPOSITORY, "backup", indexName);
			ZipFile file = new ZipFile(snapshot.getAbsolutePath());
			String pathToRepo = Config.getStringProperty("es.path.repo","test-resources");
			File tempDir = new File(pathToRepo);
			esIndexAPI.uploadSnapshot(file, tempDir.getAbsolutePath());
		} catch (DotStateException | IllegalArgumentException | IOException e) {
			e.printStackTrace();
		}finally{
			snapshot.delete();
		}
	}

	private String getLiveIndex(){
		Set<String> indexesList = esIndexAPI.listIndices();
		for(String index: indexesList){
			if(index.toLowerCase().startsWith("live")){
				return index;
			}
		}
		return null;
	}

	@AfterClass
	public static void cleanUpTests(){
		String pathToRepo = Config.getStringProperty("es.path.repo","test-resources");
		File baseDirectory = new File(pathToRepo);
		File toDelete = baseDirectory.getParentFile();
		try {
			FileUtil.deleteDir(toDelete.getAbsolutePath());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
