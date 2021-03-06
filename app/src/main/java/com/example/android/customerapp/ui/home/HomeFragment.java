package com.example.android.customerapp.ui.home;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.android.customerapp.R;
import com.example.android.customerapp.VideoPlayerActivity;
import com.example.android.customerapp.models.LodingDialog;
import com.example.android.customerapp.models.Recipe;
import com.example.android.customerapp.models.RecipeIngredient;
import com.example.android.customerapp.viewmodels.HomeViewModel;

import org.imaginativeworld.whynotimagecarousel.CarouselItem;
import org.imaginativeworld.whynotimagecarousel.ImageCarousel;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {
    private HomeViewModel mHomeViewModel;
    private ImageView iconVideo, iconVegan, iconFb, iconIg;
    private List<Recipe> mRecipeList;
    private LodingDialog lodingDialog;
    private ImageCarousel carousel;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mHomeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        carousel = root.findViewById(R.id.carousel);
        iconVideo = root.findViewById(R.id.icon_video);
        iconVegan = root.findViewById(R.id.icon_vegan);
        iconFb = root.findViewById(R.id.icon_fb);
        iconIg = root.findViewById(R.id.icon_ig);
        mRecipeList = new ArrayList<>();
        lodingDialog = new LodingDialog(getContext());
        List<CarouselItem> list = new ArrayList<>();
        list.add(new CarouselItem(R.drawable.banner1));
        list.add(new CarouselItem(R.drawable.banner2));
        list.add(new CarouselItem(R.drawable.banner3));
        carousel.addData(list);
        iconVegan.setOnClickListener(v -> {
            Toast.makeText(getContext(), "尚未啟用", Toast.LENGTH_SHORT).show();
        });
        iconFb.setOnClickListener(v -> {
            Uri uri = Uri.parse("https://www.facebook.com/tsohuecook");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        });
        iconIg.setOnClickListener(v -> {
            Uri uri = Uri.parse("https://www.instagram.com/tsohue_cook/");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        });
        iconVideo.setOnClickListener(v -> {
            lodingDialog.show();
            mHomeViewModel.getRecipeList();
            mHomeViewModel.mRecipeList.observe(getViewLifecycleOwner(), recipeList -> {
                mRecipeList = recipeList;
                String[] recipeNameList = new String[mRecipeList.size()];
                int index = 0;
                for (Recipe recipe : mRecipeList) {
                    recipeNameList[index] = recipe.getName() + "  " + recipe.getVersion();
                    index += 1;
                }
                AlertDialog.Builder dialog_list = new AlertDialog.Builder(getContext());
                dialog_list.setTitle("請選擇要導覽的食譜");
                dialog_list.setItems(recipeNameList, (dialog, index1) -> {
                    mHomeViewModel.getRecipeById(String.valueOf(mRecipeList.get(index1).getId()));
                    mHomeViewModel.mRecipe.observe(getViewLifecycleOwner(), recipe -> {
                        dialog.cancel();
                        ingredientCheck(recipe);
                    });
                });
                lodingDialog.dismiss();
                dialog_list.show();
            });
        });

        return root;
    }

    public void ingredientCheck(Recipe recipe) {
        //確認份數
        final EditText editText = new EditText(getContext());
        editText.setGravity(Gravity.RIGHT);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        final AlertDialog alertDialog = new AlertDialog.Builder(getContext()).setTitle("請輸入份數")
                .setIcon(R.drawable.icon)
                .setView(editText)
                .setPositiveButton("確定", (dialog, num) -> {
                    String input = editText.getText().toString();
                    if (input.equals("")) {
                        Toast.makeText(getContext(), "份數最少為1", Toast.LENGTH_LONG).show();
                    } else {
                        String string = "";
                        for (RecipeIngredient ri : recipe.getRecipeIngredients()) {
                            string += ri.getIngredient().getName() + (int) ri.getQuantityRequired() * Integer.valueOf(input) + ri.getIngredient().getUnit() + "\r\n";
                        }
                        AlertDialog a = new AlertDialog.Builder(getContext()).setTitle("請準備以下食材(" + input + "人份)")
                                .setMessage(string)
                                .setPositiveButton("確定", (dialog1, num1) -> {
                                    Intent intent = new Intent();
                                    intent.setClass(getContext(), VideoPlayerActivity.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable("recipe", recipe);
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                })
                                .setNegativeButton("取消", (dialog1, num1) -> {
                                    dialog1.cancel();
                                }).create();
                        dialog.cancel();
                        a.setOnShowListener(dialog12 -> {
                            Button positiveButton = a.getButton(AlertDialog.BUTTON_POSITIVE);
                            positiveButton.setTextColor(Color.DKGRAY);
                            Button negativeButton = a.getButton(AlertDialog.BUTTON_NEGATIVE);
                            negativeButton.setTextColor(Color.DKGRAY);
                        });
                        a.show();
                    }
                })
                .setNegativeButton("取消", (dialog, num) -> {
                    dialog.cancel();
                }).create();
        alertDialog.setOnShowListener(dialog -> {
            Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positiveButton.setTextColor(Color.DKGRAY);
            Button negativeButton = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
            negativeButton.setTextColor(Color.DKGRAY);
        });
        alertDialog.show();

    }
}