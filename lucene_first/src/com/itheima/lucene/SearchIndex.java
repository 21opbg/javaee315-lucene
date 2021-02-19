package com.itheima.lucene;

import java.io.File;

import javax.naming.directory.SearchResult;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.junit.Before;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class SearchIndex {
	
	private IndexSearcher indexSearcher;
	@Before
	public void init() throws Exception {
		// 1、创建一个IndexReader对象，以读的方式打开索引库。
		IndexReader indexReader = DirectoryReader.open(FSDirectory.open(new File("C:\\Users\\lenovo\\Downloads\\api\\temp\\JavaEE315\\index"))); 
		// 2、创建一个IndexSearcher对象。
		indexSearcher = new IndexSearcher(indexReader);
	}

	private void searchResult(Query query) throws Exception {
		// 4、执行查询
		TopDocs topDocs = indexSearcher.search(query, 10);
		// 5、取查询结果的总记录数
		System.out.println("查询结果的总记录数：" + topDocs.totalHits);
		// 6、打印结果
		for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
			//根据文档的id取文档对象
			Document document = indexSearcher.doc(scoreDoc.doc);
			System.out.println(document.get("name"));
			System.out.println(document.get("content"));
			System.out.println(document.get("path"));
			System.out.println(document.get("size"));
		}
		// 7、关闭IndexReader对象。
		indexSearcher.getIndexReader().close();
	}

	@Test
	public void testMatchAllDocsQuery() throws Exception {
		
		// 3、创建一个Query对象，创建一个MatchAllDocsQuery
		Query query = new MatchAllDocsQuery();
		System.out.println(query.toString());
		searchResult(query);
	}
	
	@Test
	public void testNumericRangeQuery() throws Exception {
		//创建一个Query对象
		//参数1：要查询的域 
		//参数2：范围的最小值 
		//参数3：范围的最大值
		// 参数4：是否包含最小值
		// 参数5：是否包含最大值
		Query query = NumericRangeQuery.newLongRange("size", 0l, 1000l, false, true);
		System.out.println(query);
		//执行查询
		searchResult(query);
	}
	
	@Test
	public void testBooleanQuery() throws Exception {
		BooleanQuery query = new BooleanQuery();
		//条件1
		Query query1 = new TermQuery(new Term("name", "apache"));
		//条件2
		Query query2 = new TermQuery(new Term("content", "apache"));
		//组合条件
//		query.add(query1, Occur.MUST);
		query.add(query1, Occur.MUST_NOT);
//		query.add(query2, Occur.MUST);
		query.add(query2, Occur.SHOULD);
		System.out.println(query);
		//执行查询
		searchResult(query);
	}
	@Test
	/**
	 * 带分析器的查询：先分词再进行查询。
	 * @throws Exception
	 */
	public void testQueryParser() throws Exception {
		// 1）添加QueryParser的jar包。
		// 2）创建一个QueryParser对象，两个参数。参数1：默认搜索域，参数2：分析器对象
		QueryParser queryParser = new QueryParser("content", new IKAnalyzer());
		// 3）使用QueryParser对象的parse方法创建一个Query对象，参数要搜索的内容，可以是一句话。
//		Query query = queryParser.parse("lucene是一个基于java开发的全文检索工具包");
//		Query query = queryParser.parse("name:spring");
//		Query query = queryParser.parse("content:apache");
//		Query query = queryParser.parse("*:*");//查询全部文档
//		Query query = queryParser.parse("{0 TO 1000]");//不能使用
//		Query query = queryParser.parse("+name:apache +content:apache");
//		Query query = queryParser.parse("name:apache AND content:apache");//同上
//		Query query = queryParser.parse("name:apache content:apache");
//		Query query = queryParser.parse("name:apache OR content:apache");//同上
//		Query query = queryParser.parse("-name:apache content:apache");
//		Query query = queryParser.parse("NOT name:apache content:apache");//同上
		Query query = queryParser.parse("NOT name:apache NOT content:apache");
		System.out.println(query.toString());
		// 4）执行查询
		searchResult(query);
	}
	
	@Test
	public void testMultiFieldQueryParser() throws Exception {
		//创建一个MultiFieldQueryParser对象
		String[] fields = {"name","content"};
		//参数1：字符串数组可以设置多个默认搜索域
		//参数2：分析器对象
		MultiFieldQueryParser queryParser = new MultiFieldQueryParser(fields, new IKAnalyzer());
//		Query query = queryParser.parse("lucene是一个基于java开发的全文检索工具包");
		Query query = queryParser.parse("name:apache");
		System.out.println(query);
		//执行查询
		searchResult(query);
	}
}
