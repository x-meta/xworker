<?xml version='1.0' encoding='UTF-8'?>
<urlset xmlns='http://www.google.com/schemas/sitemap/0.84'>
  <#list htmls as html>
  <url>
     <loc>${html.url}</loc>
     <lastmod>${html.date}</lastmod>
     <changefreq>daily</changefreq>
     <priority>1.0</priority>
  </url>
  </#list>	
</urlset>  