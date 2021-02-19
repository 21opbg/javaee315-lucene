package com.itheima.lucene;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Test;

public class SolrFirst {

	@Test
	/**
	 * 添加/更新也是这个方法
	 */
	public void addDocument() throws Exception {
		
		// 3、创建一个SolrServer对象。和服务器建立一个连接。HttpSolrServer
		//http://localhost:8080/solr默认是Collection1
		//http://localhost:8080/solr/collection2,推荐
		SolrServer solrServer = new HttpSolrServer("http://localhost:8080/solr/collection1");
		// 4、创建一个SolrInputDocument对象
		SolrInputDocument document = new SolrInputDocument();
		// 5、向文档中添加域
		document.addField("id", "2");
		document.addField("title", "测试title2");
		document.addField("content", "新添加的内容");
		// 6、把文档对象写入索引库
		solrServer.add(document);
		// 7、提交
		solrServer.commit();
	}
	@Test
	public void deleteDocument() throws Exception {
		// 1、创建一个SolrServer对象
		SolrServer solrServer = new HttpSolrServer("http://localhost:8080/solr/collection1");
		// 2、使用SolrServer的deleteById方法删除
//		solrServer.deleteById("change.me");
		//根据查询删除
		solrServer.deleteByQuery("id:1");
		// 3、提交
		solrServer.commit();
	}
}