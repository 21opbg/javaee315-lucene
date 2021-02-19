package com.itheima.lucene;

import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.junit.Test;

public class SolrSearchTest {

	@Test
	public void testSearch() throws Exception {
		// 1）创建一个SolrServer对象
		SolrServer solrServer = new HttpSolrServer("http://localhost:8080/solr/collection1");
		// 2）创建一个SolrQuery对象
		SolrQuery query = new SolrQuery();
		// 3）在SolrQuery中设置查询条件，可以参考后台设置
		//query.setQuery("*:*");
		query.set("q", "*:*");
		// 4）执行查询
		QueryResponse queryResponse = solrServer.query(query);
		// 5）得到一个查询结果
		SolrDocumentList solrDocumentList = queryResponse.getResults();
		// 6）取查询结果的总记录数
		System.out.println("查询结果的总记录数：" + solrDocumentList.getNumFound());
		// 7）遍历结果列表
		for (SolrDocument solrDocument : solrDocumentList) {
			System.out.println(solrDocument.get("id"));
			System.out.println(solrDocument.get("title"));
			System.out.println(solrDocument.get("content"));
		}
		//
	}
	@Test
	public void testSearch2() throws Exception {
		//创建一个SolrServer对象
		SolrServer solrServer = new HttpSolrServer("http://localhost:8080/solr/collection1");
		//创建一个查询对象
		SolrQuery query = new SolrQuery();
		query.setQuery("小黄人");
		//query.set("fq", "product_price:[0 TO 10]");
		query.addFilterQuery("product_price:[0 TO 10]");
		query.setSort("product_price", ORDER.asc);
		query.setStart(0);
		query.setRows(10);
		//设置默认搜索域
		query.set("df", "product_keywords");
		//开启高亮
		query.setHighlight(true);
		query.addHighlightField("product_name");
		query.setHighlightSimplePre("<em>");
		query.setHighlightSimplePost("</em>");
		//执行查询
		QueryResponse queryResponse = solrServer.query(query);
		SolrDocumentList solrDocumentList = queryResponse.getResults();
		//取高亮结果
		Map<String, Map<String, List<String>>> highlighting = queryResponse.getHighlighting();
		//取总记录数
		System.out.println("查询结果总记录数：" + solrDocumentList.getNumFound());
		for (SolrDocument solrDocument : solrDocumentList) {
			System.out.println(solrDocument.get("id"));
			//取高亮结果
			List<String> list = highlighting.get(solrDocument.get("id")).get("product_name");
			String name = "";
			if (list != null && list.size() > 0) {
				name = list.get(0);
			} else {
				name = (String) solrDocument.get("product_name");
			}
			System.out.println(name);
			System.out.println(solrDocument.get("product_price"));
			System.out.println(solrDocument.get("product_catalog_name"));
			System.out.println(solrDocument.get("product_picture"));
		}
	}
	
}
