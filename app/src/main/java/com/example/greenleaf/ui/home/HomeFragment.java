package com.example.greenleaf.ui.home;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.greenleaf.Adapters.BookAdapter;
import com.example.greenleaf.Adapters.MyAdapter;
import com.example.greenleaf.Model.Livre;
import com.example.greenleaf.R;
import com.example.greenleaf.global.Global;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    /*
        Accueil : contient les livres à vendre et permet d'effectuer des recherches par titres ou par auteurs
     */
    private HomeViewModel homeViewModel;
    private RecyclerView recyclerView;
    private List<Livre> listLivre;
    private MyAdapter myadapter;
    private View root;

    private String[] listItems, listPriceItems;
    private boolean[] checkedItems, checkedPriceItems;
    private int chosedPosition;
    private ArrayList<Integer> mUserItems = new ArrayList<>();

    private List <String> checkedCategories;
    private List <Livre> finalList;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        root = inflater.inflate(R.layout.fragment_home, container, false);


        recyclerView = root.findViewById(R.id.list);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        listItems = getResources().getStringArray(R.array.books_categories);
        checkedItems = new boolean[listItems.length];

        listPriceItems = getResources().getStringArray(R.array.shopping_items);
        checkedPriceItems = new boolean[listPriceItems.length];
        chosedPosition = listPriceItems.length -1;
        checkedPriceItems[chosedPosition] = true;

        DividerItemDecoration di = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        di.setDrawable(getResources().getDrawable(R.drawable.r_divider));
        recyclerView.addItemDecoration(di);

        getBooksFromFirebase();
        verifyDatabaseCategries();

        return root;
    }

    private void verifyDatabaseCategries(){
        checkedCategories = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference("Users").child(Global.currentUserId).child("Categories").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot npsnapshot : dataSnapshot.getChildren()){
                        String value = (String) npsnapshot.getValue();
                        checkedCategories.add(value);
                    }
                }
                if(checkedCategories.size() !=0 ){
                    for(int i=0; i<checkedCategories.size(); i++){
                        checkedItems[i] = checkedCategories.contains(listItems[i]);
                    }
                    filterRecommender(checkedCategories);
                }
                else
                    choseCategories();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }



    private void chose_price(){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
        mBuilder.setTitle("Séléctionnez l'intervale de prix qui vous intéressent");

        //içi on fait ce qu'on veut aprés sélécetionner un item
        mBuilder.setSingleChoiceItems(listPriceItems, chosedPosition, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        chosedPosition = which;
                    }
                });

        mBuilder.setCancelable(false);
        mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                filterRecommender(checkedCategories);
            }
        });

        mBuilder.setNegativeButton("rejeter", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int position) {
                dialog.dismiss();
            }
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }




    private void choseCategories(){

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(root.getContext());
        mBuilder.setTitle(R.string.dialog_categories_label);

        //içi on fait ce qu'on veut aprés séléctionner un item
        mBuilder.setMultiChoiceItems(listItems, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int position, boolean isChecked) {
                if(isChecked){
                    if(! mUserItems.contains(position)){
                        mUserItems.add(position);
                    }

                }else{
                    if(mUserItems.contains(position)){
                        mUserItems.remove(mUserItems.indexOf(position));
                    }
                }
            }
        });

        mBuilder.setCancelable(false);
        mBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Recevoir les categories choisis par l'utilisateur
                checkedCategories = new ArrayList<>();
                for(int i = 0; i<mUserItems.size(); i++){
                    checkedCategories.add(listItems[mUserItems.get(i)]);
                }

                filterRecommender(checkedCategories);
            }
        });

        mBuilder.setNegativeButton("REJETER", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int position) {

                dialog.dismiss();
            }
        });
        //supprimer les sÃ©lections de l'utilisateur
        mBuilder.setNeutralButton("DECOCHER TOUT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                for (int i = 0; i < checkedItems.length; i++){
                    checkedItems[i] = false;
                    mUserItems.clear();
                }

            }
        });
        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    private void filterRecommender(List<String> checkedCategories) {
        finalList = new ArrayList<>();

        String[] parts;
        int part1 =0;
        int part2 =0;
        String price = listPriceItems[chosedPosition];
        if(!price.equals("Tout prix")){
            parts = price.split("-");
            part1 = Integer.parseInt(parts[0]);
            part2 = Integer.parseInt(parts[1]);
        }


        for(int i = 0; i < listLivre.size(); i++) {
            if (checkedCategories.contains(listLivre.get(i).getCategorie())) {
                if(price.equals("Tout prix")){
                    finalList.add(listLivre.get(i));
                }
                else{
                    if(Float.parseFloat(listLivre.get(i).getPrix()) >= part1 && Float.parseFloat(listLivre.get(i).getPrix()) <= part2){
                        finalList.add(listLivre.get(i));
                    }
                }

            }
        }

        if(!checkedCategories.isEmpty())
            FirebaseDatabase.getInstance().getReference("Users").child(Global.currentUserId).child("Categories").setValue(checkedCategories);

        CreateRecycleViewContent(finalList);
    }

    private void getBooksFromFirebase(){
        listLivre = new ArrayList<>();

        final DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Livre");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot npsnapshot : dataSnapshot.getChildren()){
                        Livre l=npsnapshot.getValue(Livre.class);
                        if(!l.getUserId().equals(Global.currentUserId) && l.getAcheteurId()==null)
                            listLivre.add(l);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void CreateRecycleViewContent(List<Livre> list){
        myadapter= new MyAdapter(list);
        recyclerView.setAdapter(myadapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.customize_category,menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    showInputMethod(view.findFocus());

                }
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                myadapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.cust_categories){
            choseCategories();
            return true;
        }else if(item.getItemId() == R.id.cust_prices){
            chose_price();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void showInputMethod(View view) {
        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(view, 0);
        }
    }
}



