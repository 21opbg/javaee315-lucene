package com.itheima.lucene;

import java.io.File;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Before;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class IndexManager {
private IndexWriter indexWriter;
	
	@Before
	public void init() throws Exception {
		// 1）创建一个Directory对象，指定索引库的位置
		Directory directory = FSDirectory.open(new File("C:\\Users\\lenovo\\Downloads\\api\\temp\\JavaEE315\\index"));
		// 2）创建一个IndexWriter对象，directory,IndexWriterConfig
		IndexWriterConfig config = new IndexWriterConfig(Version.LATEST, new IKAnalyzer());
		indexWriter = new IndexWriter(directory, config);
	}

	@Test
	public void testAddDocument() throws Exception {
		
		// 3）创建一个document对象
		Document document = new Document();
		// 4）向Document对象中添加域
		Field fieldName = new TextField("name", "apache测试文档01测试文档01测试文档01测试文档01测试文档01测试文档01测试文档01测试文档01测试文档01测试文档01测试文档01", Store.YES);
		fieldName.setBoost(10);
		Field fieldContent = new TextField("content", "测试文档01的内容", Store.YES);
		Field fieldContent2 = new TextField("content2", "测试文档01的第二个内容域", Store.YES);
		Field fieldSize = new TextField("size", 1000l + "", Store.YES);
		document.add(fieldName);
		document.add(fieldContent);
		document.add(fieldContent2);
		// 5）把文档对象写入索引库
		indexWriter.addDocument(document);
		// 6）关闭IndexWriter对象
		indexWriter.close();
	}
	@Test
	public void testDeleteAllDocument() throws Exception {
		indexWriter.deleteAll();
		indexWriter.close();
	}
	@Test
	public void testDeleteDocumentByQuery() throws Exception {
		// 1）创建一个IndexWriter对象
		// 2）创建一个Query对象，可以使用TermQuery
		Query query = new TermQuery(new Term("name", "apache"));
		// 3）执行删除，查询到多少个文档就删除多少文档。
		indexWriter.deleteDocuments(query);
		// 4）关闭IndexWriter
		indexWriter.close();
	}
	@Test
	public void testUPdateDocument() throws Exception {
		// 1）创建一个IndexWriter对象
		// 2）创建一个Document对象，并添加域
		Document document = new Document();
		document.add(new TextField("name", "更新之后的文档01", Store.YES));
		document.add(new TextField("content", "更新之后的文档01内容", Store.YES));
		document.add(new TextField("name2", "更新之后的文档01", Store.YES));
		// 3）使用IndexWriter对象的update方法更新文件。
		indexWriter.updateDocument(new Term("content", "apache"), document);
		// 参数1：Term，相当于查询
		// 参数2：Document
		indexWriter.close();
	}
}
