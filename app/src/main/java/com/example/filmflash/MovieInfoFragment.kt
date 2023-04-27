package com.example.filmflash

import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.filmflash.databinding.FragmentMovieDetailsBinding

class MovieInfoFragment : Fragment() {

    private var _binding: FragmentMovieDetailsBinding? = null
    private val binding get() = _binding!!
    private var movieID = 0

    private lateinit var viewModel: MovieInfoViewModel
    private lateinit var reviewsViewModel: MovieReviewsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentMovieDetailsBinding.inflate(inflater, container, false)
        arguments?.let {
            movieID = it.getInt("movieID")
        }

        viewModel = ViewModelProvider(this)[MovieInfoViewModel::class.java]
        viewModel.getMovieInfo(movieID)

        reviewsViewModel = ViewModelProvider(this)[MovieReviewsViewModel::class.java]
        reviewsViewModel.getReviewsList(requireContext(), movieID)

        viewModel.movieInfo.observe(this, Observer {
            binding.apply {
                movieTitle.text = it.title
                movieRatingBar.rating = (it.vote_average/2).toFloat()
                val backdropPath = "https://image.tmdb.org/t/p/original/" + it.backdrop_path.toString()
                Glide.with(requireContext()).load(backdropPath).into(movieBackdrop)
                val hours = it.runtime/60
                val mins = it.runtime%60
                movieLength.text = hours.toString() + "h " + mins.toString() +"m"
                if(it.adult) {
                    movieAdult.text = "Yes"
                } else {
                    movieAdult.text = "No"
                }
                movieRating.text = String.format("%.1f", it.vote_average) + "/10"
                movieVotes.text = it.vote_count.toString() + " votes"
                if (it.tagline.isNullOrBlank()) {
                    movieTagline.visibility = View.GONE
                }
                movieReleaseDate.text = it.release_date
                var genreText = ""
                for ((index, genre) in it.genres.withIndex()) {
                    genreText += genre.name
                    if (index != it.genres.size - 1) {
                        genreText += ", "
                    }
                }
                movieGenres.text = genreText
                movieOverview.text = it.overview
                var languageText = ""
                for ((index, language) in it.spoken_languages.withIndex()) {
                    languageText += language.english_name
                    if (index != it.spoken_languages.size - 1) {
                        languageText += ", "
                    }
                }
                movieLanguages.text = languageText


            }



        })

        reviewsViewModel.reviewList.observe(this, Observer {
            binding.apply {
                reviewsTitle.text = "Reviews (" + it.size.toString() + ")"
                val reviewRV = reviewListRv
                if (it.size > 0) {
                    noReviewsText.visibility = View.GONE
                    reviewRV.visibility = View.VISIBLE
                }
                it?.let{
                    val adapter = MovieReviewsAdapter(it)
                    reviewRV.adapter = adapter
                }
            }
        })
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}