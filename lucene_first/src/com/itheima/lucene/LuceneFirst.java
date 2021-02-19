package com.itheima.lucene;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

/**
 * 查询磁盘上的文件，根据关键词查询，在文件名或者文件内容中包含某个关键词的文件
 * 建立好索引库，创建好Directory对象，指定索引库放在那里（内存或磁盘，一般是磁盘）
 * 创建一个IndexWriter对象，
 * 		IndexWriterConfig对象（两个参数lucene的版本号）
 *      分析器对象
 * 
 * 3、遍历文件夹中的文件对应每个文件创建一个文档对象。
   4、向文档对象中添加域，文件的每个属性都对应一个域。
   5、把文档对象写入索引库
   6、关闭IndexWriter对象。
 * @author lenovo
 *
 */
/**
 * 创建索引库
 * @author lenovo
 *
 */
public class LuceneFirst {
	@Test
	public void createIndex()throws Exception{
		//Directory directory = new RAMDirectory();
		Directory directory = FSDirectory.open(new File("C:\\Users\\lenovo\\Downloads\\api\\temp\\JavaEE315\\index"));
//		Analyzer analyzer = new StandardAnalyzer();
		Analyzer analyzer = new IKAnalyzer();
		IndexWriterConfig config = new IndexWriterConfig(Version.LATEST, analyzer);
		IndexWriter indexWriter = new IndexWriter(directory, config);
		File fileDir = new File("C:\\Users\\lenovo\\Downloads\\api\\searchsource\\searchsource");
		File[] listFiles = fileDir.listFiles();
		for(File file :listFiles){
			String fileName = file.getName();
			String filePath = file.getPath();
			String fileContent = FileUtils.readFileToString(file);
			long fileSize = FileUtils.sizeOf(file);
//			Field fieldName = new TextField("name",fileName,Store.YES);
//			Field fieldPath = new TextField("path",filePath,Store.YES);
//			Field fieldContent = new TextField("Content",fileContent,Store.YES);
//			Field fieldSize = new TextField("size",fileSize+"",Store.YES);
			
			Field fieldName = new TextField("name", fileName, Store.YES);
			Field fieldPath = new StoredField("path", filePath);
			Field fieldContent = new TextField("content", fileContent, Store.NO);
			Field fieldSize = new LongField("size", fileSize, Store.YES);
			
			Document document = new Document();
			document.add(fieldName);
			document.add(fieldPath);
			document.add(fieldContent);
			document.add(fieldSize);
			indexWriter.addDocument(document);
		}
		indexWriter.close();
	}
	/**
	 * 查询索引库,复杂的查询会用到分析器，创建索引库也会用到分析器
	 * @throws Exception
	 */
	@Test
	public void testSearchIndex() throws Exception {
		// 1）创建一个Directory对象，索引库的位置
		Directory directory = FSDirectory.open(new File("C:\\Users\\lenovo\\Downloads\\api\\temp\\JavaEE315\\index"));
		// 2）创建一个IndexReader对象，以读的方式打开索引库
		IndexReader indexReader = DirectoryReader.open(directory);
		// 3）创建一个IndexSearcher对象，构造参数IndexReader对象。
		IndexSearcher indexSearcher = new IndexSearcher(indexReader);
		// 4）创建一个Query对象，指定要搜索的域及要搜索的关键词。
		//参数1：要查询的域 参数2：要查询的关键词
		Query query = new TermQuery(new Term("content", "apache"));
		System.out.println("----------"+query.toString());
//		Query query = new TermQuery(new Term("name", "检"));
		// 5）执行查询，得到一个文档的id列表
		//参数1：查询对象 参数2：返回结果的最大数量
		TopDocs topDocs = indexSearcher.search(query, 10);
		//取查询结果的总记录数
		System.out.println("查询结果总记录数：" + topDocs.totalHits);
		ScoreDoc[] scoreDocs = topDocs.scoreDocs;
		for (ScoreDoc scoreDoc : scoreDocs) {
			//取文档的id
			int docId = scoreDoc.doc;
			// 6）根据id取文档对象
			Document document = indexSearcher.doc(docId);
			// 7）从文档对象中取域的内容。
			System.out.println(document.get("name"));
			//System.out.println(document.get("content"));
			System.out.println(document.get("path"));
			System.out.println(document.get("size"));
		}
		// 8）关闭IndexReader对象
		indexReader.close();
	}
	/**
	 * 中文分析器
	 * @throws Exception
	 */
	@Test
	public void testTokenStream() throws Exception {
		// 1）创建一个分析器对象。
//		Analyzer analyzer = new StandardAnalyzer();
//		Analyzer analyzer = new CJKAnalyzer();
//		Analyzer analyzer = new SmartChineseAnalyzer();
		Analyzer analyzer = new IKAnalyzer();
		// 2）调用分析器的对象的tokenStream方法获得一个TokenStream对象，参数就是要分析的的内容。
		//参数1：域的名称，可以是null
		TokenStream tokenStream = analyzer.tokenStream(null, "2015年11月29日 - Lucene是传智播客apache软件基金会4 jakarta项目组的一个法轮功子项目,是一个开放源代码的全文检索引擎工具包");
//		TokenStream tokenStream = analyzer.tokenStream(null, "The Spring Framework provides a comprehensive programming and configuration model");
		// 3）调用tokenStream对象的reset方法
		tokenStream.reset();
		// 4）给指针设置一个引用
		CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
		// 5）遍历单词列表
		while(tokenStream.incrementToken()) {
			// 6）取引用的内容
			System.out.println(charTermAttribute.toString());
		}
		// 7）关闭tokenStream对象
		tokenStream.close();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
