//Book Class to create book items
package ragnaorok.Main;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.ArrayList;

public class Book {
    String title;
    String author;
    String currentPage = "";
    int numPages = 0;
    int numLines = 0;
    ArrayList<String> pages = new ArrayList<String>();
    ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
    BookMeta bookMeta = (BookMeta) book.getItemMeta();

    public String getTitle()
    {
        return title;
    }
    public String getAuthor()
    {
        return author;
    }
    public int getNumPages()
    {
        return numPages;
    }
    public ArrayList<String> getPages(){
        return pages;
    }
    public BookMeta getBookMeta(){
        return (BookMeta) book.getItemMeta();
    }
    public void setBookMeta(Book itemStack){
        this.book = book;
        book.setItemMeta(book.getItemMeta());
    }

    public void setTitle(String title)
    {
        this.title = title;
        bookMeta.setTitle(title);
    }
    public void setAuthor(String author)
    {
        this.author = author;
        bookMeta.setAuthor(author);
    }
    public void setNumPages(int numPages)
    {
        this.numPages = numPages;
    }

    public void addPage ()
    {
        bookMeta.addPage(currentPage);
        ++numPages;
    }


    public void addToPage (int num, String line)
    {
        pages.add(num, line);
    }
    public void getPage(int i) {

    }

    public void addInfo()
    {
        book.setItemMeta(bookMeta);
    }

    public void giveBook(Player p)
    {
        if (p.getInventory().firstEmpty() != -1)
        {
            p.getInventory().addItem(book);
        }
        else
        {
            p.sendMessage("cant give you " + title + " book, your inventory is full!");
        }
    }


}
