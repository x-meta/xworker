<?xml version="1.0" encoding="utf-8"?>
<urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9"> 
  <url> 
    <loc>${loc}</loc> 
    <changefreq>always</changefreq> 
    <priority>1.0</priority> 
  </url> 
  <#list htmls as html>
  <url> 
    <loc>${html.url}</loc> 
    <lastmod>${html.date}</lastmod> 
  </url>
  </#list>
</urlset>